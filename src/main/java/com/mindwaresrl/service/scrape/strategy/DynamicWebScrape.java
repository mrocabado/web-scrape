package com.mindwaresrl.service.scrape.strategy;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import com.mindwaresrl.common.AgentDinamicWeb;
import com.mindwaresrl.common.Conversion;
import com.mindwaresrl.common.WebScrapePlaywrightManager;
import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;
import com.mindwaresrl.service.scrape.WebScrape;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class DynamicWebScrape implements WebScrape {

    @Override
    public WebScrapeResult execute(WebScrapeRequest webScrapeRequest) throws IOException {
        Browser browser = WebScrapePlaywrightManager.browser();

        // Single Optimized Attempt with Smart Profile (User-Agent + Locale + Timezone)
        try (BrowserContext context = browser.newContext(AgentDinamicWeb.createProfile())) {

            Page page = context.newPage();
            page.navigate(String.valueOf(webScrapeRequest.url()),
                    new Page.NavigateOptions()
                            .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                            .setTimeout(webScrapeRequest.timeout().toMillis()));

            String htmlContent = page.content();
            return Conversion.toWebScrapeResult(htmlContent);
        } catch (Exception e) {
            log.error("Optimized scraping attempt failed for URL: {}", webScrapeRequest.url(), e);
            throw new IOException("Failed to scrape URL: " + webScrapeRequest.url(), e);
        }
    }
}
