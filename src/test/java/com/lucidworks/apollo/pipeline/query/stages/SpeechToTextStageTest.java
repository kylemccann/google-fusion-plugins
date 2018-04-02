package com.lucidworks.apollo.pipeline.query.stages;

import com.mashape.unirest.http.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.lucidworks.apollo.pipeline.Context;
import com.lucidworks.apollo.pipeline.impl.DefaultContext;
import com.lucidworks.apollo.pipeline.query.QueryRequestAndResponse;
import com.lucidworks.apollo.rest.ExtraMediaTypes;
import com.lucidworks.apollo.rest.RequestParams;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.*;
import org.junit.rules.ExpectedException;


import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.InputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertNotNull;


public class SpeechToTextStageTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    String url;
    GoogleSpeechToTextStageConfig config;
    GoogleSpeechToTextStage stage;

    @Before
    public void setup(){
        url = "";
        config = new GoogleSpeechToTextStageConfig("testBasics", "AIzaSyBgA9dJN7iw1_9vnDPlWTntPrvSDg5idjU", GoogleSpeechToTextStageConfig.REQUEST, "testBasics",
                url, "0", "LINEAR16", true);
         stage = new GoogleSpeechToTextStage(TestHelper.newParams(config));
    }
    @Test
    public void testCreateJsonObject() throws Exception {
        GoogleSpeechToTextStageConfig config = new GoogleSpeechToTextStageConfig("", "", "", "", "", "", "", false); //We are not testing this so values dont matter.
        GoogleSpeechToTextStage sts = new GoogleSpeechToTextStage(TestHelper.newParams(config));
        
        byte[] b = new byte[0]; // dummy file.
        String jsonString = sts.createJsonObject("LINEAR16", 16000, b);
        String expectedJsonString = "{\"config\":{\"encoding\":\"LINEAR16\",\"sampleRate\":16000,\"maxAlternatives\":1,\"profanityFilter\":false},\"audio\":{\"content\":\"\"}}";
        Assert.assertEquals(expectedJsonString, jsonString);
    }


    @Test
    public void testGetResultString() throws Exception {
        QueryRequestAndResponse message = null;
        Context context = null;

        JSONObject json = new JSONObject(EXPECTED_RESPONSE);
        stage.getResultString(null,false, json);

        Assert.assertEquals("what is the difference between inner join and outer join", "what is the difference between inner join and outer join");
    }

    @Test
    public void testBadApiResponse() throws Exception {
        QueryRequestAndResponse message = null;
        Context context = null;


        JSONObject json = new JSONObject(BAD_API_KEY);

        exception.expect(Exception.class);
        exception.expectMessage("Google Speech API: The request is missing a valid API key.");
        stage.getResultString(null,false, json);

    }

    @Test
    public void testNoAudioApiResponse() throws Exception {
        QueryRequestAndResponse message = null;
        Context context = null;


        JSONObject json = new JSONObject(RecognitionAudioNotSet);

        exception.expect(Exception.class);
        exception.expectMessage("Google Speech API: RecognitionAudio not set.");
        stage.getResultString(null,false, json);

    }


    public byte[] getTestAudio() throws IOException {
        InputStream stream = null;
        try {//we don't care about the actual message, since we are using Wiremock
            stream = SpeechToTextStageTest.class.getClassLoader().getResourceAsStream("InnerOuterJoin.wav");
            return IOUtils.toByteArray(stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    //Error When API Key is incorrect
    public static final String BAD_API_KEY = "{\n" +
            "    \"error\": {\n" +
            "        \"code\": 403,\n" +
            "        \"message\": \"The request is missing a valid API key.\",\n" +
            "        \"status\": \"PERMISSION_DENIED\"\n" +
            "    }\n" +
            "}";


    //Error when Audio is not passed in
    public static final String RecognitionAudioNotSet = "{\n" +
            "    \"error\": {\n" +
            "        \"code\": 400,\n" +
            "        \"message\": \"RecognitionAudio not set.\",\n" +
            "        \"status\": \"INVALID_ARGUMENT\"\n" +
            "    }\n" +
            "}";

    //Expected response when sending request with InnerOuterJoin.wav
    public static final String EXPECTED_RESPONSE = "{\n" +
            "    \"results\": [\n" +
            "        {\n" +
            "            \"alternatives\": [\n" +
            "                {\n" +
            "                    \"transcript\": \"what is the difference between inner join and outer join\",\n" +
            "                    \"confidence\": 0.98609316\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";
}


