package com.lucidworks.apollo.pipeline.query.stages;

import com.google.cloud.speech.v1.*;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.protobuf.ByteString;
import com.lucidworks.apollo.pipeline.AutoDiscover;
import com.lucidworks.apollo.pipeline.Context;
import com.lucidworks.apollo.pipeline.StageAssistFactoryParams;
import com.lucidworks.apollo.pipeline.query.QueryRequestAndResponse;
import com.lucidworks.apollo.pipeline.query.QueryStage;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@AutoDiscover(type = GoogleSpeechToTextStageConfig.TYPE)
    public class SpeechToTextStage extends QueryStage<GoogleSpeechToTextStageConfig> {
        private static Logger log = LoggerFactory.getLogger(SpeechToTextStage.class);


        @Inject
        public SpeechToTextStage(@Assisted StageAssistFactoryParams params) {
            super(params);
        }


        @Override
        public QueryRequestAndResponse process(QueryRequestAndResponse message, Context context) {
            try {
                byte[] entityBytes = message.request.getEntityBytes();

                if (entityBytes != null) {


                    //Write Audio to file.
                    writeToFile(entityBytes);


                    // Instantiates a client
                    SpeechClient speech = SpeechClient.create();
                    GoogleSpeechToTextStageConfig stageConfig = getConfiguration();

                    // The path to the audio file to transcribe
                    Path path = Paths.get("speechToText.wav");
                    byte[] data = Files.readAllBytes(path);
                    ByteString audioBytes = ByteString.copyFrom(data);

                    // Builds the sync recognize request
                    RecognitionConfig config = RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setSampleRateHertz(16000)
                            .setLanguageCode("en-US")
                            .build();
                    RecognitionAudio audio = RecognitionAudio.newBuilder()
                            .setContent(audioBytes)
                            .build();

                    // Performs speech recognition on the audio file
                    log.info("Transcribing {}", path);
                    RecognizeResponse response = speech.recognize(config, audio);
                    List<SpeechRecognitionResult> results = response.getResultsList();
                    String resultsLocation = stageConfig.getResultsLocation();
                    final String resultsKey = stageConfig.getResultsKey();

                    switch (resultsLocation) {
                        case SpeechToTextStageConfig.REQUEST: //Return results string
                            message.request.putSingleParam(resultsKey, results.toString());
                            break;


                        case SpeechToTextStageConfig.RESPONSE: //Return entire response
                            //TODO return entire response
                            break;



                    }

                }
            }
            catch (Exception e){
                System.out.println("e = " + e);
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
