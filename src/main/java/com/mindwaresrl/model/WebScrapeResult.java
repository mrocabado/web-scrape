package com.mindwaresrl.model;

import java.time.Instant;

public record WebScrapeResult(String markdown, String title, Instant timestamp) {
    public static WebScrapeResult EMPTY_RESULT = new WebScrapeResult("undefined", "undefined", Instant.EPOCH);

    public WebScrapeResult {
        if (markdown == null || markdown.isBlank()) {
            throw new IllegalArgumentException("Missing markdown");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Missing title");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Missing timestamp");
        }
    }

}
