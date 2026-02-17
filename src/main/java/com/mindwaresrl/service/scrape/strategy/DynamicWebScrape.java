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
                .setViewportSize(1920, 1080)
                .setUserAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"))) {
            Page page = context.newPage();

            page.navigate(String.valueOf(webScrapeRequest.url()),
                    new Page.NavigateOptions()
                            .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                            .setTimeout(webScrapeRequest.timeout().toMillis()));

            String url = webScrapeRequest.url().toString();
            if (getIsPostOrPhoto(url)) {
                navigateToPost(page, url);
            }
            String htmlContent = page.content();

            return Conversion.toWebScrapeResult(htmlContent);
        }
    }

    public Boolean getIsPostOrPhoto(String url) {
        if (url == null) {
            return false;
        }
        return url.contains("posts") ||
                url.contains("photo") ||
                url.contains("photos");

    }

    public void navigateToPost(Page page, String url) {
        page.navigate(url);

        try {
            // Intenta cerrar el modal de inicio de sesión buscando el botón 'Cerrar' o
            // 'Close'
            // Se usa un timeout corto por si el modal no aparece
            page.click("div[aria-label='Close'], div[aria-label='Cerrar']",
                    new Page.ClickOptions().setTimeout(4000));
        } catch (Exception e) {
            // Ignorar si el botón no se encuentra (el modal no apareció)
        }

        // Esperar un momento para visualizar la página cargada
        page.waitForTimeout(5000);
    }

    public static void main(String[] args) {
        try {
            DynamicWebScrape dynamicWebScrape = new DynamicWebScrape();
            String urlString = "https://www.facebook.com/CBAFanPage/posts/felicidades-a-los-lectores-cbabiblioteca/1906082259562953/";

            System.out.println("Probando detección de URL: " + dynamicWebScrape.getIsPostOrPhoto(urlString));

            WebScrapeRequest webScrapeRequest = new WebScrapeRequest(
                    URI.create(urlString).toURL(),
                    Duration.of(1L, ChronoUnit.MINUTES));
            WebScrapeResult webScrapeResult = dynamicWebScrape.execute(webScrapeRequest);
            System.out.println(webScrapeResult.markdown());

            System.out.println("Navegador abierto para inspección...");
            Thread.sleep(60000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
