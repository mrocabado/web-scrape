package com.mindwaresrl.common;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;

@Slf4j
public class DomRendering {
    private  static final int TIMEOUT_MILLIS = 10000;

    /**
     * Tests if a website uses JavaScript to dynamically update its DOM
     * by comparing static HTML content with what a browser would render
     */
    public static boolean usesDynamicRendering(URL url) {
        boolean usesDynamicRendering = true;
        try {
            // Method 1: Get raw HTML (what JSoup sees)
            Document staticDoc = Jsoup.connect(String.valueOf(url))
                    .userAgent(FakeUserAgent.chrome())
                    .timeout(TIMEOUT_MILLIS)
                    .get();

            String staticHtml = staticDoc.html();

            int scriptTags = staticDoc.select("script").size();

            // Check for empty/minimal body with scripts
            int bodyTextLength = staticDoc.body().text().trim().length();
            boolean suspiciouslyEmpty = bodyTextLength < 100 && scriptTags > 0;

            // Check for common SPA root elements
            boolean hasSpaRoot = !staticDoc.select("#root").isEmpty() ||
                    !staticDoc.select("#app").isEmpty() ||
                    !staticDoc.select("[data-reactroot]").isEmpty();
            usesDynamicRendering = suspiciouslyEmpty || hasSpaRoot;
        } catch (Exception e) {
            log.error("Error detecting dynamic rendering", e);
        }

        return usesDynamicRendering;
    }
}
