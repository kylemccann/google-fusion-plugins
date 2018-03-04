package com.lucidworks.apollo.pipeline.query.stages;

import org.junit.Assert;
import org.junit.Test;


public class SpeechToTextStageConfigTest {


    @Test
    public void testSpeechToTextStageConfig(){

        String expected_id = "testID";
        String url = "http://testApi.com" ;
        String expected_apiKey = "hahdhfhfhshshc";
        String expected_sampleRate = "1";
        String expected_ResultsKey = "something";
        String expected_ResultsLocation = SpeechToTextStageConfig.REQUEST;
        String expected_Encoding = "LINEAR16";
        boolean expected_DebugMode = true;

        SpeechToTextStageConfig config = new SpeechToTextStageConfig(expected_id, expected_apiKey, expected_ResultsLocation, expected_ResultsKey,
                url, expected_sampleRate , expected_Encoding, expected_DebugMode);


        Assert.assertNotNull(config);

        Assert.assertEquals(expected_id, config.getId());
        Assert.assertEquals(url, config.getGoogleEndpoint());
        Assert.assertEquals(expected_apiKey, config.getApiKey());
        Assert.assertEquals(expected_ResultsKey, config.getResultsKey());
        Assert.assertEquals(expected_ResultsLocation, config.getResultsLocation());
        Assert.assertEquals(expected_sampleRate, config.getSampleRate());
        Assert.assertEquals(expected_Encoding, config.getEncoding());

    }

}
