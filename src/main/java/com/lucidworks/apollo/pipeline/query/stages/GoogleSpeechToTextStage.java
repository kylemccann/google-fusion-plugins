package com.lucidworks.apollo.pipeline.query.stages;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.lucidworks.apollo.pipeline.AutoDiscover;
import com.lucidworks.apollo.pipeline.Context;
import com.lucidworks.apollo.pipeline.StageAssistFactoryParams;
import com.lucidworks.apollo.pipeline.query.QueryRequestAndResponse;
import com.lucidworks.apollo.pipeline.query.QueryStage;
import com.lucidworks.apollo.pipeline.query.Response;
import com.lucidworks.apollo.pipeline.query.stages.JsonObjects.RecognitionObject;
import com.lucidworks.apollo.pipeline.query.stages.JsonObjects.RequestObject;
import com.lucidworks.apollo.solr.response.AppendableResponse;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;

@AutoDiscover(type = SpeechToTextStageConfig.TYPE)
public class GoogleSpeechToTextStage extends QueryStage<SpeechToTextStageConfig> {
    private static Logger log = LoggerFactory.getLogger("");
    protected FileHandler handler;
    protected PrintWriter logit;


    @Inject
    public GoogleSpeechToTextStage(@Assisted StageAssistFactoryParams params) {
        super(params);
    }


    @Override
    public QueryRequestAndResponse process(QueryRequestAndResponse message, Context context) {
        Boolean debugMode = false;
        try {
            byte[] entityBytes = message.request.getEntityBytes();


            if (entityBytes != null) {

                String result = null;
                logit = new PrintWriter("GoogleSpeechToText.txt");

                //Write Audio to file.
                File file = writeToFile(entityBytes);


                //Get Configuration from Fusion
                SpeechToTextStageConfig stageConfig = getConfiguration();
                debugMode = stageConfig.isDebugMode();


                // get the audio file to transcribe
                Path path = Paths.get(file.getPath());
                byte[] data = Files.readAllBytes(path);

                //Get Endpoint URL
                String endPointURL = null;
                if (stageConfig.getGoogleEndpoint() != null) {
                    endPointURL = stageConfig.getGoogleEndpoint();
                } else {
                    endPointURL = "https://speech.googleapis.com/v1beta1/speech:syncrecognize"; //Use Default
                }

                int sampleRate;
                if (stageConfig.getSampleRate() != null) {
                    sampleRate = Integer.parseInt(stageConfig.getSampleRate());
                } else {
                    sampleRate = 16000;
                }


                String encoding = stageConfig.getEncoding();
                if (encoding != null) {
                    encoding = encoding.toUpperCase();
                } else {
                    logit.println("Encoding is null");
                }

                //If debugMode = true log con fig
                logConfig(debugMode, logit, endPointURL, encoding, Integer.toString(sampleRate));

                String jsonString = createJsonObject(encoding, sampleRate, data);


                //Check Authentication
                //API Key should be passed in as param at end of Endpoint: https://cloud.google.com/docs/authentication/api-keys
                String apiKey = stageConfig.getApiKey();


                //** Google API Request **//
                HttpResponse<String> jsonResponse = Unirest.post(endPointURL + "?key=" + apiKey)
                        .header("accept", "application/json;text/xml")
                        .header("Content-Type", "audio/wav; codec=audio/pcm; samplerate=16000")
                        .body(jsonString)
                        .asString();

                JSONObject json = new JSONObject(jsonResponse.getBody());
                result = getResultString(logit, debugMode, json); //Extract String from JSON


                final String resultsLocation = stageConfig.getResultsLocation();
                final String resultsKey = stageConfig.getResultsKey();
                //Write to Results Location -
                writeToResultsLocation(message, context, result, resultsLocation, resultsKey);

            }
            logit.close();
        } catch (Exception e) {
            if (debugMode) {
                logit.println(e);
                logit.close();
            }
            log.error("Google Speech to Text Stage ERROR : " + e);
        }
        return message;
    }

    protected String getResultString(PrintWriter logit, Boolean debugMode, JSONObject jsonResponse) throws Exception {
        String result = "";
        if (jsonResponse != null ) { //Check for no response

            if(jsonResponse.names().get(0).toString().contains("error")){  //Throw Error

                String errorMessage = jsonResponse.getJSONObject("error").get("message").toString();
                if(logit!=null) {
                    logit.println("Google Speech API Error: " + errorMessage);
                }

                throw new Exception("Google Speech API: " + errorMessage);

            }else {
                JSONArray alternativesObj = jsonResponse.getJSONArray("results");
                JSONObject alternatives = alternativesObj.getJSONObject(0).getJSONArray("alternatives").getJSONObject(0);
                result = (String) alternatives.get("transcript");

            }

            if (debugMode) {
                logit.println("speech to text result: " + result);
            }

        } else {
            throw new NullPointerException("no Json returned from Google API");
        }
        return result.trim();
    }

    protected void writeToResultsLocation(QueryRequestAndResponse message, Context context, String result, String resultsLocation, String resultsKey) {
        switch (resultsLocation) {
            case SpeechToTextStageConfig.REQUEST: //Return results string
                message.request.putSingleParam(resultsKey, result);
                break;


            case SpeechToTextStageConfig.RESPONSE:
                // Add a transformer to run after the stages run
                final Function<QueryRequestAndResponse, QueryRequestAndResponse> transformer
                        = context.getProperty(Context.RESPONSE_TRANSFORMER, Function.class);
                context.setProperty("responseTransformer", new Function<QueryRequestAndResponse, QueryRequestAndResponse>() {
                    @Override
                    public QueryRequestAndResponse apply(QueryRequestAndResponse input) {
                        if (transformer != null) {
                            input = transformer.apply(input);
                        }
                        if (input == null) {
                            return null;
                        }
                        if (input.response.isPresent()) {
                            Response response = input.response.get();
                            if (response.initialEntity instanceof AppendableResponse) {
                                ((AppendableResponse) response.initialEntity).appendString(resultsKey, result.toString());
                            } else {
                                log.error("Could not add '" + resultsKey + "' to the Fusion response since it is not appendable");
                            }
                        }
                        return input;
                    }
                });

                break;
            case SpeechToTextStageConfig.CONTEXT:
                context.setProperty(resultsKey, result);
                break;
        }
    }


    protected void logConfig(Boolean enabled, PrintWriter logit, String endPointURL, String encoding, String sampleRate) {
        logit.println("Endpoint: " + endPointURL +
                " Encoding: " + encoding +
                " Sample Rate: " + sampleRate
        );
    }


    protected String createJsonObject(String encoding, int sampleRate, byte[] data) throws Exception {
        //**Create Objects to be serialised into JSON **//
        RecognitionObject recog = new RecognitionObject(encoding, sampleRate);

        //Create Request Object
        RequestObject request = new RequestObject(recog, DatatypeConverter.printBase64Binary(data)); //TODO Add in conversion for other data types

        //Turn Object into JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonString = mapper.writeValueAsString(request);

        return jsonString;
    }

    @NotNull
    protected File writeToFile(byte[] entityBytes) throws IOException {
        File audio = File.createTempFile("speechToText", ".wav");
        logit.println("File path: " + audio.getAbsolutePath() );
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(audio);
            IOUtils.write(entityBytes, fos);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        logit.println("Wrote audio to file");
        return audio;
    }


}
