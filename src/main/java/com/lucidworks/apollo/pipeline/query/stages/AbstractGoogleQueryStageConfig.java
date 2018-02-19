package com.lucidworks.apollo.pipeline.query.stages;
import com.lucidworks.apollo.pipeline.schema.Annotations;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class AbstractGoogleQueryStageConfig extends AbstractGoogleStageConfig {


    public static final String REQUEST = "Request";
    public static final String RESPONSE = "Response";
    @com.lucidworks.apollo.pipeline.schema.Annotations.SchemaProperty(
            title = RESULTS_LOCATION_TITLE,
            description = RESULTS_LOC_DESC,
            name = RESULTS_LOCATION,
            hints = {UIHints.ADVANCED},
            defaultValue = REQUEST)
    @Annotations.StringProperty(allowedValues = {REQUEST, RESPONSE, CONTEXT}) //TODO: should this be an enumerated type?
    protected final String resultsLocation;

    @Annotations.SchemaProperty(title = "Sample Rate", description = "Set this only if you wish to constrain all your audio input to one single sample rate. ")
    protected final String sampleRateHertz;

    public AbstractGoogleQueryStageConfig(String id,
                                          @JsonProperty("googleEnvironmentVar") String googleEnvironmentVar,
                                          @JsonProperty("resultsLocation") String resultsLocation,
                                          @JsonProperty("resultsKey") String resultsKey,
                                          @JsonProperty("googleEndpoint") String googleEndpoint,
                                          @JsonProperty("sampleRateHertz") String sampleRateHertz)
    {
        super(id, googleEnvironmentVar, googleEndpoint, resultsKey);
        this.resultsLocation = resultsLocation;
        this.sampleRateHertz = sampleRateHertz;

    }

    @JsonProperty("resultsLocation")
    public String getResultsLocation() {
        return resultsLocation;
    }

    @JsonProperty("sampleRateHertz")
    public String getSampleRateHertz() {
        return sampleRateHertz;
    }

}
