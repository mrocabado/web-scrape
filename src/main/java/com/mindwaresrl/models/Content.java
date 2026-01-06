package com.mindwaresrl.models;

import java.util.List;

public record Content(
        String title,
        String cleanText,
        List<String> links
) {}
