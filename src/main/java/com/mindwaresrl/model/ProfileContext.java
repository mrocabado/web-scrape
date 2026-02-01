package com.mindwaresrl.model;
import java.net.URL;
public record ProfileContext(
        URL url,
        ProfileMode mode,
        String domain
) {
}

