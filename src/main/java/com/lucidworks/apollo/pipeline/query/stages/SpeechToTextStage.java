package com.lucidworks.apollo.pipeline.query.stages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.lucidworks.apollo.pipeline.AutoDiscover;
import com.lucidworks.apollo.pipeline.Context;
import com.lucidworks.apollo.pipeline.StageAssistFactoryParams;
import com.lucidworks.apollo.pipeline.query.QueryRequestAndResponse;
import com.lucidworks.apollo.pipeline.query.QueryStage;
import com.lucidworks.apollo.pipeline.query.stages.JsonObjects.RecognitionObject;
import com.lucidworks.apollo.pipeline.query.stages.JsonObjects.RequestObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
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
import com.lucidworks.apollo.pipeline.query.stages.JsonObjects.RecognitionObject;

@AutoDiscover(type = SpeechToTextStageConfig.TYPE)
    public class SpeechToTextStage extends QueryStage<SpeechToTextStageConfig> {
        private static Logger log = LoggerFactory.getLogger("");
        protected FileHandler handler;
        protected PrintWriter logit;
        protected Boolean debugMode;

        @Inject
        public SpeechToTextStage(@Assisted StageAssistFactoryParams params) {
            super(params);
        }


        @Override
        public QueryRequestAndResponse process(QueryRequestAndResponse message, Context context) {
            try {
                byte[] entityBytes = message.request.getEntityBytes();

                if (entityBytes != null) {

                    String result = null;
                    logit = new PrintWriter("GoogleSpeechToText.txt");

                    //Write Audio to file.
                    File file = writeToFile(entityBytes);

                    SpeechToTextStageConfig stageConfig = getConfiguration();
                    debugMode = stageConfig.isDebugMode();
                    if(debugMode) {
                        logit.println(stageConfig.getGoogleEndpoint());
//                        logit.println(stageConfig.getApiKey());
                        logit.println(stageConfig.getSampleRate());
                        logit.println(stageConfig.getEncoding());
                    }


                    // The path to the audio file to transcribe
                    Path path = Paths.get(file.getPath());
                    byte[] data = Files.readAllBytes(path);

                    //Get Endpoint URL
                    String endPointURL = null;
                    if (stageConfig.googleEndpoint != null) {
                        endPointURL = stageConfig.getGoogleEndpoint();
                    } else {
                        endPointURL = "https://speech.googleapis.com/v1beta1/speech:syncrecognize"; //Use Default
                    }
                    logit.println("endPointUrl " + endPointURL);

                    int sampleRate;
                    logit.println("Getting sample rate");
                    if (stageConfig.getSampleRate() !=null){
                        sampleRate = Integer.parseInt(stageConfig.getSampleRate());
                    }else{
                        logit.println("setting sample rate to default ");
                        sampleRate = 60000;
                    }



                    String encoding = stageConfig.getEncoding();
                    if(encoding!=null){
                        encoding = encoding.toUpperCase();
                    }else{
                        logit.println("Encoding is null");
                    }

                    logit.println("Endpoint: " + endPointURL +
                            " Encoding: " + encoding +
                            " Sample Rate: " + sampleRate
                    );

                    //**Create Objects to be serialised into JSON **//
                     RecognitionObject recog = new RecognitionObject(encoding, sampleRate);

                    //Create Request Object
                    RequestObject request = new RequestObject(recog, DatatypeConverter.printBase64Binary(data)); //TODO Add in conversion for other data types

                    //Turn Object into JSON
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    String jsonString = mapper.writeValueAsString(request);

                    //Check Authentication
                    //API Key should be passed in as param at end of Endpoint: https://cloud.google.com/docs/authentication/api-keys
                    String apiKey = stageConfig.getApiKey();


                        //** Google API Request **//
                        HttpResponse<JsonNode> jsonResponse = Unirest.post(endPointURL + "?key=" + apiKey)
                                .header("accept", "application/json;text/xml")
                                .header("Content-Type", "audio/wav; codec=audio/pcm; samplerate=16000")
                                .body(jsonString)
                                .asJson();
                        if (jsonResponse != null) { //Check for no response
//                        result = jsonResponse.getBody().getObject().get("transcript");

                            //Todo find a better way of doing this
                            JSONArray resultArr = jsonResponse.getBody().getObject().getJSONArray("results");
                            JSONObject alternativesObj = resultArr.getJSONObject(0);
                            JSONArray transcript = alternativesObj.getJSONArray("alternatives");
                            JSONObject tran = transcript.getJSONObject(0);

                            result = tran.get("transcript").toString();

                            if (debugMode) {
                                logit.println("speech to text result: " + result);
                            }

                            //if Json response returns

//                        JSONObject error = (JSONObject) jsonResponse.getBody().getObject().get("error");
//                        String errorCode;
//                        String errorMessage;
//                        if(error != null){
//                            errorCode = error.get("code").toString();
//                            errorMessage = error.get("message").toString();
//                            log.error("Google S2T Error code: " + errorCode);
//                            log.error(errorMessage);
//                        }
                        }


                        // Performs speech recognition on the audio file
                        log.info("Transcribing {}", path);
//                    RecognizeResponse response = speech.recognize(config, audio);
//                    List<SpeechRecognitionResult> results = response.getResultsList();
                        String resultsLocation = stageConfig.getResultsLocation();
                        final String resultsKey = stageConfig.getResultsKey();

                        switch (resultsLocation) {
                            case SpeechToTextStageConfig.REQUEST: //Return results string
                                message.request.putSingleParam(resultsKey, result);
                                break;


                            case SpeechToTextStageConfig.RESPONSE: //Return entire response
                                //TODO return entire response
                                break;


                        }

                    }
                logit.close();
            }
            catch (Exception e){
                if(debugMode) {
                    logit.println(e);
                    logit.close();
                }
                log.error("Google Speech to Text Stage ERROR : " + e);
            }
            return message;
        }


        @NotNull
        protected File writeToFile(byte[] entityBytes) throws IOException {
            File audio = File.createTempFile("speechToText", ".wav");

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

//        private String getBestResult(SpeechResults transcript) {
//            String bestResult = null;
//            if (transcript != null) {
//                double maxConfidence = Double.MIN_VALUE;
//                for (Transcript item : transcript.getResults()) {
//                    for (SpeechAlternative speechAlternative : item.getAlternatives()) {
//                        if (speechAlternative.getConfidence() > maxConfidence) {
//                            maxConfidence = speechAlternative.getConfidence();
//                            bestResult = speechAlternative.getTranscript();
//                        }
//                    }
//                }
//            } else {
//                log.warn("Unable to process transcript: {}", transcript);
//            }
//            log.info("Transcribed best result: " + bestResult);
//            if (bestResult != null) {
//                bestResult = bestResult.trim();
//            }
//            return bestResult;
//        }
//
//    }

    }
