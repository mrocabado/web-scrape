package com.mindwaresrl.models;

public record FetchMetadata(
        int statusCode,
        long loadTimeMs,
        int retriesUsed,
        String strategy
) {}
