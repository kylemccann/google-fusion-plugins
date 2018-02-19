package com.lucidworks.apollo.pipeline.query.stages;

import com.google.protobuf.ByteString;

public class RecognitionAudio {
    private ByteString content;

    public RecognitionAudio(ByteString content) {
        this.content = content;
    }
}
