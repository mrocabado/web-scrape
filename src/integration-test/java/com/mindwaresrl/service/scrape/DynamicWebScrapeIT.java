package com.mindwaresrl.service.scrape;

import com.mindwaresrl.common.WebScrapePlaywrightManager;
import com.mindwaresrl.model.WebScrapeRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class DynamicWebScrapeIT {

    @BeforeAll
    public static void setUp() throws IOException {
        WebScrapePlaywrightManager.browser();
    }

    @AfterAll
    public static void tearDown() {
        WebScrapePlaywrightManager.closeAll();
    }

    @Test
    void givenScrapeRequest_whenDynamicWebScrapeExecutes_thenResultIsReturned() throws InterruptedException, IOException {
        //GIVEN
        var webSearchRequest = new WebScrapeRequest(URI.create("https://www.facebook.com/CBAFanPage/posts/felicidades-a-los-lectores-cbabiblioteca/1906082259562953/").toURL(), Duration.of(1L, ChronoUnit.MINUTES) );

        //WHEN
        var webSearch = new DynamicWebScrape();
        var webSearchResult = webSearch.execute(webSearchRequest);

        //THEN
        assertThat(webSearchResult).isNotNull();
        assertThat(webSearchResult.markdown()).isNotNull();

        System.out.println(webSearchResult.markdown());
    }
}
