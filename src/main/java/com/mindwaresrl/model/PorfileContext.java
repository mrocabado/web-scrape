package com.mindwaresrl.model;
import java.net.URL;
public record PorfileContext(
        URL url,
        ProfileMode mode,
        String domain
) {
}

