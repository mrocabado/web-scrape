package com.mindwaresrl.models;
import java.util.List;
import java.util.Optional;


public record ScrapeResult(
        String url,
        boolean success,
        Optional<FetchMetadata> metadata,
        Optional<Content> content,
        Optional<String> error
) {
    public static ScrapeResult failure(String url, String errorMsg) {
        return new ScrapeResult(
                url,
                false,
                Optional.empty(),
                Optional.empty(),
                Optional.ofNullable(errorMsg)
        );
    }
}





