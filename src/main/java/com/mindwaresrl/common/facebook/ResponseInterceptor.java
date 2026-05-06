package com.mindwaresrl.common.facebook;

import com.microsoft.playwright.Response;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResponseInterceptor {

    private final List<String> capturedBodies = new CopyOnWriteArrayList<>();
    private int seen = 0;
    private int matchedUrl = 0;
    private int matchedBody = 0;

    public void handle(Response response) {
        seen++;
        if (!isRelevantUrl(response)) return;
        matchedUrl++;
        captureBody(response);
    }

    public List<String> getCapturedBodies() {
        System.out.println("\n========== [INTERCEPTOR] RESUMEN ==========");
        System.out.println("[INTERCEPTOR] Respuestas vistas:    " + seen);
        System.out.println("[INTERCEPTOR] Match por URL:        " + matchedUrl);
        System.out.println("[INTERCEPTOR] Match por keyword:    " + matchedBody);
        System.out.println("[INTERCEPTOR] Bodies capturados:    " + capturedBodies.size());
        System.out.println("===========================================\n");
        return capturedBodies;
    }

    private boolean isRelevantUrl(Response response) {
        if (response.status() != 200) return false;
        String url = response.url();
        return Arrays.stream(FacebookSelectors.AJAX_URL_PATTERNS).anyMatch(url::contains);
    }

    private void captureBody(Response response) {
        try {
            String body = response.text();
            if (!hasKeyword(body)) return;
            matchedBody++;
            capturedBodies.add(body);
            System.out.println("[INTERCEPTOR] CAPTURADO (" + body.length() + " bytes) <- "
                    + truncate(response.url(), 100));
        } catch (Exception e) {
            // El browser ya cerró o el response expiró; lo ignoramos silenciosamente.
            if (e.getMessage() != null && e.getMessage().contains("No resource with given identifier")) {
                return;
            }
            System.out.println("[INTERCEPTOR] Error leyendo body: " + e.getMessage());
        }
    }

    private boolean hasKeyword(String body) {
        return Arrays.stream(FacebookSelectors.BODY_KEYWORDS).anyMatch(body::contains);
    }

    private String truncate(String s, int max) {
        return s.substring(0, Math.min(s.length(), max));
    }
}