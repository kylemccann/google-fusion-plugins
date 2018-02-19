package com.lucidworks.apollo.pipeline.query.stages;

import com.google.protobuf.ByteString;

//This is used to create a JSON request object to send to the API.
public class RequestObject {

    RecognitionObject config;
    RecognitionAudio audio;

    public RequestObject (RecognitionObject config, ByteString audioString){
        this.config = config;

        audio = new RecognitionAudio(audioString);

        /** RecognitionConfig
         * {
         "encoding": enum(AudioEncoding),
         "sampleRate": number,
         "languageCode": string,
         "maxAlternatives": number,
         "profanityFilter": boolean,
         "speechContext": {
         object(SpeechContext)
         },
         }*/
    }
}
