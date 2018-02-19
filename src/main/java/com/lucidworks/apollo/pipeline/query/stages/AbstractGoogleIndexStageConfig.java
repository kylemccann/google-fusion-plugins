/**
 *
 *
 **/
package com.lucidworks.apollo.pipeline.query.stages;
import com.lucidworks.apollo.pipeline.schema.Annotations;
import com.lucidworks.apollo.pipeline.schema.UIHints;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Base class with some helpful, common setters for index stages.
 *
 * See https://github.com/watson-developer-cloud/java-sdk
 **/
public abstract class AbstractGoogleIndexStageConfig extends AbstractGoogleStageConfig {

  public static final String DOCUMENT = "Document";

  @Annotations.SchemaProperty(
          title = RESULTS_LOCATION_TITLE,
          description = RESULTS_LOC_DESC,
          name = AbstractGoogleStageConfig.RESULTS_LOCATION,
          hints = {UIHints.ADVANCED},
          defaultValue = DOCUMENT)
  @Annotations.StringProperty(allowedValues = {DOCUMENT, CONTEXT}) //TODO: should this be an enumerated type?
  protected final String resultsLocation;


  public AbstractGoogleIndexStageConfig(String id, @JsonProperty("googleEnvironmentVar") String googleEnvironmentVar,
                                        @JsonProperty("resultsLocation") String resultsLocation,
                                        @JsonProperty("resultsKey") String resultsKey,
                                        @JsonProperty("googleEndpoint") String googleEndpoint)
  {
    super(id, googleEnvironmentVar, googleEndpoint, resultsKey);
    this.resultsLocation = resultsLocation;
  }

  @JsonProperty("resultsLocation")
  public String getResultsLocation() {
    return resultsLocation;
  }

}
