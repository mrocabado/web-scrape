// trigger_face.java - MODIFICADO
package com.mindwaresrl.common;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class trigger_face {
    public trigger_face(Page page, String url) {
        routeToScraper(page, url);
    }

    private void routeToScraper(Page page, String url) {
        if (url.contains("photo/")) {
            new Photo_Facebook(page, url);
            return;
        }

        scrapeNonPhoto(page, url);
    }

    private void scrapeNonPhoto(Page page, String url) {
        page.navigate(url);
        page.waitForTimeout(3000);
        removeDialogs(page);

        if (url.contains("/posts/")) {
            new Post_facebook(page,url);
        }
    }

    public static void removeDialogs(Page page) {
        Locator dialog = page.locator("div[role='dialog']");
        if (dialog.count() > 0) {
            page.evaluate("document.querySelectorAll(\"div[role='dialog']\").forEach(e => e.remove());");
            page.evaluate("document.body.style.overflow='auto'");
        }
    }
}