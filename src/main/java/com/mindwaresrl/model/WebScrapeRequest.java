package com.mindwaresrl.model;

import java.net.URL;
import java.time.Duration;

public record WebScrapeRequest(URL url, Duration timeout) {
    private static final Long MAX_TIMEOUT_MINUTES = 5L;

    public WebScrapeRequest {
        if (url == null) {
            throw new IllegalArgumentException("Missing URL");
        }

        if (timeout == null) {
            throw new IllegalArgumentException("Missing timeout");
        }
        if (timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("Invalid timeout");
        }
        if (timeout.compareTo(Duration.ofMinutes(MAX_TIMEOUT_MINUTES)) > 0) {
            throw new IllegalArgumentException("Timeout greater than " + MAX_TIMEOUT_MINUTES + " minutes");
        }
    }

}
