package com.mindwaresrl.service.scrape;

import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;

import java.io.IOException;

public class DynamicWebScrape implements WebScrape {

    @Override
    public WebScrapeResult execute(WebScrapeRequest webScrapeRequest) throws IOException {
        return WebScrapeResult.EMPTY_RESULT;
    }
}
