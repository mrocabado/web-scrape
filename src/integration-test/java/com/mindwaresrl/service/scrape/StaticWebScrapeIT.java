package com.mindwaresrl.service.scrape;

import com.mindwaresrl.model.WebScrapeRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class StaticWebScrapeIT {

    @Test
    void givenScrapeRequest_whenStaticWebScrapeExecutes_thenResultIsReturned() throws InterruptedException, IOException {
        //GIVEN
        var webSearchRequest = new WebScrapeRequest(URI.create("https://www.lostiempos.com/actualidad/pais/20260107/dialogo-gobierno-cob-se-reanuda-este-jueves").toURL(), Duration.of(1L, ChronoUnit.MINUTES) );

        //WHEN
        var webSearch = new StaticWebScrape();
        var webSearchResult = webSearch.execute(webSearchRequest);

        //THEN
        assertThat(webSearchResult).isNotNull();
        assertThat(webSearchResult.markdown()).isNotNull();

        System.out.println(webSearchResult.markdown());
    }
}
