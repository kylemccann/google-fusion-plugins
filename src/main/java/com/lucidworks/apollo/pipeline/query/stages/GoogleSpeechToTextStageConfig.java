package com.lucidworks.apollo.pipeline.query.stages;
import com.lucidworks.apollo.pipeline.schema.Annotations;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;


    /**
     * See https://github.com/google-developer-cloud/java-sdk
     **/
    @JsonTypeName(GoogleSpeechToTextStageConfig.TYPE)
    @Annotations.Schema(
            type = GoogleSpeechToTextStageConfig.TYPE,
            title = "Google Speech to Text",
            description = "This stage forwards Audio content to the Google Speech To Text Service and either converts to text and adds to a parameter on the request or puts the results into the pipeline context for downstream handling."
    )
    public class GoogleSpeechToTextStageConfig extends AbstractGoogleQueryStageConfig {

        public static final String TYPE = "google-speech-to-text";

        @Annotations.SchemaProperty(title = "Extract Best String", description = "If true, than the best string is extracted and added using the results key, if false, the full Watson response (SpeechResults object) is stored under the results key.  If the results location is REQUEST or RESPONSE, then that SpeechResults object will be converted to a String representation.",
                defaultValue = "true",
                hints = {UIHints.ADVANCED})
        protected Boolean extractString;


        @JsonCreator
        public GoogleSpeechToTextStageConfig(@JsonProperty("id") String id,
                                             @JsonProperty("googleEnvironmentVar") String googleEnvironmentVar,
                                             @JsonProperty("resultsLocation") String resultsLocation,
                                             @JsonProperty("resultsKey") String resultsKey,
                                             @JsonProperty("extractString") Boolean extractString,
                                             @JsonProperty("googleEndpoint") String googleEndpoint,
                                             @JsonProperty("sampleRateHertz") String sampleRateHertz)
        {
            super(id, googleEnvironmentVar, resultsLocation, resultsKey, googleEndpoint, sampleRateHertz);
            this.extractString = extractString;
        }

        @JsonProperty("extractString")
        public Boolean isExtractString() {
            return extractString;
        }

    }
