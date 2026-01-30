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
        int maxRetries = AgentDinamicWeb.getTotalAgents();
        Exception lastException = null;

        for (int i = 0; i < maxRetries; i++) {
            String currentUserAgent = AgentDinamicWeb.getNextAgent();
            try (BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(1920, 1080)
                    .setUserAgent(currentUserAgent))) {

                Page page = context.newPage();
                page.navigate(String.valueOf(webScrapeRequest.url()),
                        new Page.NavigateOptions()
                                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                                .setTimeout(webScrapeRequest.timeout().toMillis()));

                String htmlContent = page.content();
                return Conversion.toWebScrapeResult(htmlContent);
            } catch (Exception e) {
                log.warn("Attempt {}/{} failed with User Agent: {}. Error: {}", i + 1, maxRetries, currentUserAgent,
                        e.getMessage());
                lastException = e;
            }
        }

        throw new IOException("Failed to scrape after " + maxRetries + " attempts. Last error: "
                + (lastException != null ? lastException.getMessage() : "Unknown"), lastException);
    }
}
