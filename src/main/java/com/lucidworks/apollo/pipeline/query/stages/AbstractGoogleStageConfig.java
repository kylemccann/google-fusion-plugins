package com.lucidworks.apollo.pipeline.query.stages;
import com.lucidworks.apollo.pipeline.StageConfig;
import com.lucidworks.apollo.pipeline.schema.Annotations;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 *
 **/
public abstract class AbstractGoogleStageConfig extends StageConfig {

  public static final String CONTEXT = "Context";
  public static final String INPUT_LOCATION = "inputLocation";
  public static final String RESULTS_LOCATION = "resultsLocation";
  public static final String RESULTS_LOCATION_TITLE = "Results Location";
  public static final String RESULTS_LOC_DESC = "The location to put the results.  Whichever is chosen, the results will be stored using the Results Key.";
  @Annotations.SchemaProperty(title = "Google API key", description = "This allows you to authenticate with the Google API")
  protected  String apiKey;
  @Annotations.SchemaProperty(title = "Google Endpoint URL",
          hints = {UIHints.ADVANCED},
          description = "The Google endpoint to use.  Only set if not using Google's default settings.",
          defaultValue = "https://speech.googleapis.com/v1beta1/speech:syncrecognize"
          //From the SpeechToText.URL in the Google java-sdk.
          )
  protected String googleEndpoint;
  @Annotations.SchemaProperty(title = "Results Key", description = "The name of the key to store the results object under.  See the documentation for the type of objects stored.",
          defaultValue = "q",
          required = true)
  protected String resultsKey;



  public AbstractGoogleStageConfig(String id, @JsonProperty("apiKey") String apiKey, @JsonProperty("googleEndpoint") String googleEndpoint, @JsonProperty("resultsKey") String resultsKey ) {
    super(id);
    this.apiKey = apiKey;
    this.googleEndpoint = googleEndpoint;
    this.resultsKey = resultsKey;

  }


  @JsonProperty("resultsKey")
  public String getResultsKey() {
    return resultsKey;
  }

  @JsonProperty("apiKey")
  public String getApiKey() { return apiKey; }

  @JsonProperty("googleEndpoint")
  public String getGoogleEndpoint() {
    return googleEndpoint;
  }




}