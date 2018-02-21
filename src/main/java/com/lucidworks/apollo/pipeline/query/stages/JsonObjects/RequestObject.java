package com.lucidworks.apollo.pipeline.query.stages.JsonObjects;

//This is used to create a JSON request object to send to the API.
public class RequestObject {

    RecognitionObject config;
    RecognitionAudio audio;

    public RequestObject (RecognitionObject config, String audioString){
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

    public RecognitionObject getConfig() {
        return config;
    }

    public void setConfig(RecognitionObject config) {
        this.config = config;
    }

    public RecognitionAudio getAudio() {
        return audio;
    }

    public void setAudio(RecognitionAudio audio) {
        this.audio = audio;
    }
}
