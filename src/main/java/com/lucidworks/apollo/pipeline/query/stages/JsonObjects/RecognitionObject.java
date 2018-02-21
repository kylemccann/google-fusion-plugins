package com.lucidworks.apollo.pipeline.query.stages.JsonObjects;

public class RecognitionObject {
    //More info at: https://cloud.google.com/speech/reference/rest/v1beta1/RecognitionConfig
    public String encoding = "LINEAR16";
    private int sampleRate = 16000; //Required - 16000 is optimal.
    private String languageCode; //Optional
    private int maxAlternatives = 1; //Optional
    boolean profanityFilter; //Optional
    //Todo add SpeechContext Object


    public RecognitionObject(String encoding, int sampleRate) {
        this.encoding = encoding;
        this.sampleRate = sampleRate;
    }

    public RecognitionObject(String encoding, int sampleRate, String languageCode, int maxAlternatives, boolean profanityFilter) {

        this.encoding = encoding;
        this.sampleRate = sampleRate;
        this.languageCode = languageCode;
        this.maxAlternatives = maxAlternatives;
        this.profanityFilter = profanityFilter;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public int getMaxAlternatives() {
        return maxAlternatives;
    }

    public void setMaxAlternatives(int maxAlternatives) {
        this.maxAlternatives = maxAlternatives;
    }

    public boolean isProfanityFilter() {
        return profanityFilter;
    }

    public void setProfanityFilter(boolean profanityFilter) {
        this.profanityFilter = profanityFilter;
    }
}


