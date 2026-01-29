package com.mindwaresrl.service.scrape.strategy;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import com.mindwaresrl.common.Conversion;
import com.mindwaresrl.common.WebScrapePlaywrightManager;
import com.mindwaresrl.common.UserAgentStrategy;
import com.mindwaresrl.common.user_agent_update;
import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;
import com.mindwaresrl.service.scrape.WebScrape;

import java.io.IOException;

public class DynamicWebScrape implements WebScrape {

    @Override
    public WebScrapeResult execute(WebScrapeRequest webScrapeRequest) throws IOException {
        Browser browser = WebScrapePlaywrightManager.browser();

        //TODO we need a way to update user agent
        //UserAgentStrategy uaStrategy = user_agent_update.getStrategy(webScrapeRequest);
        //String currentUserAgent = uaStrategy.getUserAgent();
        try (BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                //.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
          //      .setUserAgent(currentUserAgent)
        )) {
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
