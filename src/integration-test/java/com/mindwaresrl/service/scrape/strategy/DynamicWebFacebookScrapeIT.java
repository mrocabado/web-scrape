package com.mindwaresrl.service.scrape.strategy;

import com.mindwaresrl.common.WebScrapePlaywrightManager;
import com.mindwaresrl.model.WebScrapeRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

class DynamicWebFacebookScrapeIT {

    @BeforeAll
    public static void setUp() throws IOException {
        // Ensure browser is initialized.
        // Note: For visual debugging, ensure WebScrapePlaywrightManager configures
        // headless=false
        WebScrapePlaywrightManager.browser();
    }

    @AfterAll
    public static void tearDown() {
        WebScrapePlaywrightManager.closeAll();
    }

    @Test
    void givenFacebookScrapeRequest_whenDynamicWebScrapeExecutes_thenResultIsReturned()
            throws InterruptedException, IOException {
        // Facebook post URL for testing
        String testUrl = "https://m.facebook.com/CBAFanPage/posts/felicidades-a-los-lectores-cbabiblioteca/1906082259562953/";

        var webSearchRequest = new WebScrapeRequest(
                URI.create(testUrl).toURL(),
                Duration.of(1L, ChronoUnit.MINUTES));

        // WHEN
        var webSearch = new DynamicWebScrape();
        var webSearchResult = webSearch.execute(webSearchRequest);

        // THEN
        System.out.println("Scraping Result Markdown:");
        System.out.println(webSearchResult.markdown());

        // Optional: Wait to observe the browser state before it closes in tearDown
        // Thread.sleep(5000);
    }
}