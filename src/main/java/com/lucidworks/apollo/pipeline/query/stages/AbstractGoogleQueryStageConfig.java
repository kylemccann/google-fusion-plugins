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
    @Annotations.StringProperty(allowedValues = {REQUEST, RESPONSE, CONTEXT})
    protected final String resultsLocation;




    public AbstractGoogleQueryStageConfig(String id,
                                          @JsonProperty("apiKey") String apiKey,
                                          @JsonProperty("resultsLocation") String resultsLocation,
                                          @JsonProperty("resultsKey") String resultsKey,
                                          @JsonProperty("googleEndpoint") String googleEndpoint
                                          )
    {
        super(id, apiKey, googleEndpoint, resultsKey);
       this.resultsLocation = resultsLocation;
    }

    @JsonProperty("resultsLocation")
    public String getResultsLocation() {
        return resultsLocation;
    }



}
