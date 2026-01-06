package com.mindwaresrl.extractors;

import com.microsoft.playwright.Page;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;



public class LinkExtractor {

    @SuppressWarnings("unchecked")
    public List<String> extract(Page page, String baseDomain) {
        try {

            String jsScript = "function(elements) { return elements.map(e => e.href); }";

            Object rawResult = page.locator("a[href]").evaluateAll(jsScript);

            if (rawResult == null) {
                return Collections.emptyList();
            }

            List<String> allLinks = (List<String>) rawResult;

            return allLinks.stream()
                    .filter(link -> link != null && link.contains(baseDomain))
                    .distinct()
                    .toList();

        } catch (Exception e) {

            System.err.println("⚠️ Warning: No se pudieron extraer links. Error JS: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}