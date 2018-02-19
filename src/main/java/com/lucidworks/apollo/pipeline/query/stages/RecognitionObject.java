package com.lucidworks.apollo.pipeline.query.stages;

public class RecognitionObject {
    //More info at: https://cloud.google.com/speech/reference/rest/v1beta1/RecognitionConfig
    private Enum encoding; //Required -
    private int sampleRate; //Required
    private String languageCode; //Optional
    private int maxAlternatives; //Optional
    boolean profanityFilter; //Optional
    //Todo add SpeechContext Object


    public RecognitionObject(Enum encoding, int sampleRate) {
        this.encoding = encoding;
        this.sampleRate = sampleRate;
    }

    public RecognitionObject(Enum encoding, int sampleRate, String languageCode, int maxAlternatives, boolean profanityFilter) {
        this.encoding = encoding;
        this.sampleRate = sampleRate;
        this.languageCode = languageCode;
        this.maxAlternatives = maxAlternatives;
        this.profanityFilter = profanityFilter;
    }


}
