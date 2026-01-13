package com.mindwaresrl.service.scrape;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import com.mindwaresrl.common.Conversion;
import com.mindwaresrl.common.WebScrapePlaywrightManager;
import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;

import java.io.IOException;

public class DynamicWebScrape implements WebScrape {

    @Override
    public WebScrapeResult execute(WebScrapeRequest webScrapeRequest) throws IOException {
        Browser browser = WebScrapePlaywrightManager.browser();

        try (BrowserContext context = browser.newContext()) {
            Page page = context.newPage();

            page.navigate(String.valueOf(webScrapeRequest.url()),
                    new Page.NavigateOptions()
                    .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                    .setTimeout(webScrapeRequest.timeout().toMillis()));

            String htmlContent = page.content();

            return Conversion.toWebScrapeResult(htmlContent);
        }
    }
}
