package com.lucidworks.apollo.pipeline.query.stages;
import com.lucidworks.apollo.pipeline.schema.Annotations;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;


    /**
     * See https://github.com/google-developer-cloud/java-sdk
     **/
    @JsonTypeName(SpeechToTextStageConfig.TYPE)
    @Annotations.Schema(
            type = SpeechToTextStageConfig.TYPE,
            title = "Google Speech to Text",
            description = "This stage forwards Audio content to the Google Speech To Text Service and either converts to text and adds to a parameter on the request or puts the results into the pipeline context for downstream handling."
    )
    public class SpeechToTextStageConfig extends AbstractGoogleQueryStageConfig {

        public static final String TYPE = "google-speech-to-text";

        public static final String LINEAR16 = "LINEAR16";
        public static final String FLAC = "FLAC";
        public static final String MULAW = "MULAW";
        public static final String AMR = "AMR";
        public static final String AMR_WB = "AMR_WB";
        public static final String OGG_OPUS = "OGG_OPUS";
        public static final String SPEEX_WITH_HEADER_BYTE = "SPEEX_WITH_HEADER_BYTE";

        @com.lucidworks.apollo.pipeline.schema.Annotations.SchemaProperty(
                title = "Encoding",
                description = "Please see Google documentation, regarding recommended encoding",
                name = "Encoding",
                hints = {UIHints.ADVANCED},
                defaultValue = LINEAR16)
        @Annotations.StringProperty(allowedValues = {LINEAR16, FLAC, MULAW, AMR, AMR_WB, OGG_OPUS, SPEEX_WITH_HEADER_BYTE})
        protected  String encoding;

        @Annotations.SchemaProperty(title = "Debug Mode", description = "This enables verbose error logging", defaultValue = "FALSE")
        protected  Boolean debugMode;

        @Annotations.SchemaProperty(title = "Sample Rate", description = "Set this only if you wish to constrain all your audio input to one single sample rate. ", required = true)
        protected  String sampleRate;

        @JsonCreator
        public SpeechToTextStageConfig(@JsonProperty("id") String id,
                                       @JsonProperty("apiKey") String apiKey,
                                       @JsonProperty("resultsLocation") String resultsLocation,
                                       @JsonProperty("resultsKey") String resultsKey,
                                       @JsonProperty("extractString") Boolean extractString,
                                       @JsonProperty("googleEndpoint") String googleEndpoint,
                                       @JsonProperty("sampleRate") String sampleRate,
                                       @JsonProperty("encoding") String encoding,
                                       @JsonProperty("debugMode") Boolean debugMode)
        {
            super(id, apiKey, resultsLocation, resultsKey, googleEndpoint);

            this.sampleRate = sampleRate;
            this.encoding = encoding;
            this.debugMode = debugMode;
        }


        @JsonProperty("encoding")
        public String getEncoding() {
            return encoding;
        }

        @JsonProperty("debugMode")
        public Boolean isDebugMode() {
            return debugMode;
        }

        @JsonProperty("sampleRate")
        public String getSampleRate() {
            return sampleRate;
        }
    }
