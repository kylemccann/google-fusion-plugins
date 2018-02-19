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
  @Annotations.SchemaProperty(title = "Google Environment Variable", description = "This should be the location of the Google authentication JSON")
  protected final String googleEnvironmentVar;
  @Annotations.SchemaProperty(title = "Google Endpoint URL",
          hints = {UIHints.ADVANCED},
          description = "The Google endpoint to use.  Only set if not using Google's default settings."
          //From the SpeechToText.URL in the Google java-sdk.
          )
  protected final String googleEndpoint;
  @Annotations.SchemaProperty(title = "Results Key", description = "The name of the key to store the results object under.  See the documentation for the type of objects stored.",
          defaultValue = "googleResults",
          required = true)
  protected String resultsKey;



  public AbstractGoogleStageConfig(String id, @JsonProperty("googleEnvironmentVar") String googleEnvironmentVar, @JsonProperty("googleEndpoint") String googleEndpoint, @JsonProperty("resultsKey") String resultsKey) {
    super(id);
    this.googleEnvironmentVar =  googleEnvironmentVar;
    this.googleEndpoint = googleEndpoint;
    this.resultsKey = resultsKey;


  }



  @JsonProperty("resultsKey")
  public String getResultsKey() {
    return resultsKey;
  }

  @JsonProperty("googleEnvironmentVar")
  public String getGoogleEnvironmentVar() { return System.getenv("GOOGLE_APPLICATION_CREDENTIALS"); //TODO Show error if this is null;
  }

  @JsonProperty("googleEndpoint")
  public String getGoogleEndpoint() {
    return googleEndpoint;
  }




}