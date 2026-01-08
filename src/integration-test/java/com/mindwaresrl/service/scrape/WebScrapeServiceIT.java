package com.mindwaresrl.service.scrape;

import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class WebScrapeServiceIT {

    @Test
    void givenSimpleScrape_whenWebScrapeServiceExecutes_thenResultIsReturned() throws MalformedURLException {
        // GIVEN
//        var webSearchRequest = new WebScrapeRequest(URI.create("https://mrocabado.github.io/web/auto_mpg.html").toURL(), Duration.of(1L, ChronoUnit.MINUTES) );
        var webSearchRequest = new WebScrapeRequest(URI.create("https://www.lostiempos.com/actualidad/pais/20260107/dialogo-gobierno-cob-se-reanuda-este-jueves").toURL(), Duration.of(1L, ChronoUnit.MINUTES) );

        // WHEN
        var webScrapeService = new WebScrapeService();
        var webScrapeResult = webScrapeService.scrape(webSearchRequest);

        // THEN
        assertThat(webScrapeResult).isNotNull();
        assertThat(webScrapeResult).isNotSameAs(WebScrapeResult.EMPTY_RESULT);

        System.out.println(webScrapeResult.markdown());
    }
}
