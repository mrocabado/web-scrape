package com.mindwaresrl.service.scrape.strategy;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import com.mindwaresrl.common.Conversion;
import com.mindwaresrl.common.WebScrapePlaywrightManager;
import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;
import com.mindwaresrl.service.scrape.WebScrape;

import com.mindwaresrl.common.trigger_face;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class DynamicWebScrape implements WebScrape {

    @Override
    public WebScrapeResult execute(WebScrapeRequest webScrapeRequest) throws IOException {
        Browser browser = WebScrapePlaywrightManager.browser();

        // TODO we need a way to update user agent
        try (BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                        .setLocale("es-Es")
                        .setTimezoneId("America/La_Paz")
                //.setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1")
                //.setViewportSize(390, 844)
        )

        ) {
            context.addInitScript( "Object.defineProperty(navigator, 'webdriver', { get: () => false })");

            Page page = context.newPage();
            new trigger_face(page,webScrapeRequest.url().toString());

            page.navigate(String.valueOf(webScrapeRequest.url()),
                    new Page.NavigateOptions()
                           // .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                            .setWaitUntil(WaitUntilState.NETWORKIDLE) //proof
                            .setTimeout(webScrapeRequest.timeout().toMillis()));


            page.waitForTimeout(8000);


            String htmlContent = page.content();

            return Conversion.toWebScrapeResult(htmlContent);
        }
    }


    public static void main(String[] args) {
        try {
            DynamicWebScrape dynamicWebScrape = new DynamicWebScrape();
            String urlString = "https://www.facebook.com/photo/?fbid=25803701135968038&set=gm.35174597642139249&idorvanity=3227683947257367";
            WebScrapeRequest webScrapeRequest = new WebScrapeRequest(
                    URI.create(urlString).toURL(),
                    Duration.of(1L, ChronoUnit.MINUTES));
            WebScrapeResult webScrapeResult = dynamicWebScrape.execute(webScrapeRequest);
          System.out.println(webScrapeResult.markdown());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
