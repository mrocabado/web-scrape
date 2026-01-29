package com.mindwaresrl.model;

public record BrowserProfile(
        String userAgent,
        int width,
        int height,
        String locale,
        String timezone
) {



}
