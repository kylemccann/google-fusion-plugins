# Google Speech to Text Stage for Lucidworks Fusion
Lucidworks Fusion stages for Google Speech to Text API.


# Prerequisites

1. You will need an Google Developer account.   https://developers.google.com
1. Lucidworks Fusion 3.0 or higher.  Download at http://lucidworks.com.
 
 
# Installation

1. Edit ~/.gradle/gradle.properties and add 
  ```fusionHome=<PATH TO WHERE FUSION IS INSTALLED>```
1. ./gradlew install
1. Restart Fusion

Note, if you have a multi-node Fusion installation, you will need to copy the jars to each node and set the classpath entries accordingly.  See the build.gradle file for info on what files are updated.


# Usage

This plugin currently consist of 1 query pipeline stages: Speech To text.  To use it, add the stage to an
existing Query Pipeline and fill in the appropriate parameters.  See below for more details.


## Speech To Text

As the name implies, the Speech To Text stage converts audio (we only accept audio/wav MIME type so far) and converts it to text, which
can then be used downstream in the pipeline for querying or other usages.

## API Usage

The Speech To Text stage works by taking in audio/wav MIME type objects and transcribing them to text.  Thus, to use the stage, you need
to submit an audio file to a query pipeline configured to use the Speech To Text stage (see an example configuration below).

See our documentation for more detail, especially:
1. https://doc.lucidworks.com/fusion/3.0/Search/Query-Pipelines.html
1. https://doc.lucidworks.com/fusion/3.0/Pipeline_Stages_Reference/Query-Pipeline-Stages.html
1. https://doc.lucidworks.com/fusion/3.0/REST_API_Reference/Query-Pipelines-API.html

### Example API usage.

Assuming you have a collection named "watson" setup and the Speech To Text stage configured in the default pipeline, you can do the following:


    curl -X POST -H "Content-Type: audio/wav" -u USER:PASS --data-binary @/PATH/TO/WAV/FILE "http://localhost:8764/api/apollo/query-pipelines/default/collections/google/select"

### Example Configuration
Here's an example configuration, with user and password XXXXXX'd out.  Note that transcribing audio can take some time.

    {
      "id" : "Google",
      "stages" : [ {
        "type" : "speech-to-text",
        "id" : "zo3ld8kamakeqgds4i",
        "username" : "xXx-Redacted-xXx",
        "password" : "xXx-Redacted-xXx",
        "resultsLocation" : "Request",
        "resultsKey" : "q",
        "extractString" : true,
        "type" : "speech-to-text",
        "skip" : false,
        "label" : "speech-to-text",
        "secretSourceStageId" : "zo3ld8kamakeqgds4i"
      }, {
        "type" : "recommendation",
        "id" : "99294a9e-65e5-4e3e-b415-954c0c4e35bf",
        "numRecommendations" : 10,
        "numSignals" : 100,
        "aggrType" : "*",
        "boostId" : "id",
        "type" : "recommendation",
        "skip" : false,
        "label" : "recommendation",
        "secretSourceStageId" : "99294a9e-65e5-4e3e-b415-954c0c4e35bf"
      }, {
        "type" : "search-fields",
        "id" : "77778df6-b753-457b-8c32-180ff505a566",
        "rows" : 10,
        "start" : 0,
        "queryFields" : [ ],
        "returnFields" : [ "*", "score" ],
        "type" : "search-fields",
        "skip" : false,
        "label" : "search-fields",
        "secretSourceStageId" : "77778df6-b753-457b-8c32-180ff505a566"
      }, {
        "type" : "facet",
        "id" : "16ed9919-e894-41d3-9119-309efd7b606b",
        "fieldFacets" : [ ],
        "type" : "facet",
        "skip" : false,
        "label" : "facet",
        "secretSourceStageId" : "16ed9919-e894-41d3-9119-309efd7b606b"
      }, {
        "type" : "solr-query",
        "id" : "38a85e5f-9955-489a-97cb-5426efbb2321",
        "allowedRequestHandlers" : [ ],
        "httpMethod" : "POST",
        "allowFederatedSearch" : false,
        "type" : "solr-query",
        "skip" : false,
        "label" : "solr-query",
        "secretSourceStageId" : "38a85e5f-9955-489a-97cb-5426efbb2321"
      } ],
      "properties" : {
        "secretSourcePipelineId" : "google"
      }
    }



