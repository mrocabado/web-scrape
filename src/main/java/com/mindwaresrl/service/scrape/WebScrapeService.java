package com.mindwaresrl.service.scrape;

import com.mindwaresrl.common.DomRendering;
import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@Slf4j
public class WebScrapeService {

    public WebScrapeResult scrape(WebScrapeRequest webScrapeRequest) {
        Objects.requireNonNull(webScrapeRequest, "WebScrapeRequest cannot be null");

        WebScrape webScrape = getWebScrapeStrategy(webScrapeRequest.url());

        return getWebScrapeResult(webScrapeRequest, webScrape);
    }

    private WebScrape getWebScrapeStrategy(URL url) {
        if (DomRendering.usesDynamicRendering(url)) {
            return new DynamicWebScrape();
        } else {
            return new StaticWebScrape();
        }
    }

    private static WebScrapeResult getWebScrapeResult(WebScrapeRequest webScrapeRequest, WebScrape webScrape) {
        try {
            log.info("Using {} strategy for URL: {}", webScrape.getClass().getSimpleName(), webScrapeRequest.url());
            return webScrape.execute(webScrapeRequest);
        } catch (IOException e) {
            log.error("Running and consolidating web search results interrupted", e);
            return WebScrapeResult.EMPTY_RESULT;
        }
    }
}
