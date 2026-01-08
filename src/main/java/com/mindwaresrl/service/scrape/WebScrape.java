package com.mindwaresrl.service.scrape;

import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;

import java.io.IOException;

public interface WebScrape {
    WebScrapeResult execute(WebScrapeRequest webScrapeRequest) throws IOException;;
}
