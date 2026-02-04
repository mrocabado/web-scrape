package com.mindwaresrl.common;

import com.mindwaresrl.model.PageSnapshot;

import java.util.List;

public class DefaultBlockDetector implements BlockDetector{
    private static final List<String> BLOCK_KEYWORDS = List.of(
            "access denied",
            "request blocked",
            "verify you are human",
            "unusual traffic",
            "enable javascript",
            "captcha",
            "checking your browser"
    );
    @Override
    public boolean isBLocked(PageSnapshot result) {
        return isBlockedByStatus(result)
                || isBlockedByUrl(result)
                || isBlockedByHtml(result)
                || isBlockedByHeuristics(result);
    }

    private boolean isBlockedByStatus(PageSnapshot snapshot) {
        int status = snapshot.statusCode();
        return status == 401 || status == 403 || status == 429;
    }

    private static final List<String> URL_BLOCK_MARKERS = List.of(
            "captcha",
            "challenge",
            "verify"
    );

    private boolean isBlockedByUrl(PageSnapshot snapshot) {
        return snapshot.finalURL() != null
                && containsAny(snapshot.finalURL(), URL_BLOCK_MARKERS);
    }

    private boolean containsAny(String value, List<String> markers) {
        String lower = value.toLowerCase();
        return markers.stream().anyMatch(lower::contains);
    }


    private boolean isBlockedByHtml(PageSnapshot snapshot) {
        return snapshot.html() == null || snapshot.html().isBlank();
    }

    private boolean isBlockedByHeuristics(PageSnapshot snapshot) {
        if (snapshot.contentLength() < 5_000) {
            return true;
        }

        String htmlLower = snapshot.html().toLowerCase();
        return BLOCK_KEYWORDS.stream()
                .anyMatch(htmlLower::contains);
    }


}
