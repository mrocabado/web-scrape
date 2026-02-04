package com.mindwaresrl.model;

public record PageSnapshot(
        String html,
        String finalURL,
        int statusCode,
        long contentLength
) {

}
