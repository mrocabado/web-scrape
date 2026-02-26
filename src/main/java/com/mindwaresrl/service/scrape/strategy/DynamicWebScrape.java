package com.mindwaresrl.service.scrape.strategy;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
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
                        .setLocale("es-Es")
                        .setTimezoneId("America/La_Paz")
                .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1")
                .setViewportSize(390, 844)
        )

        ) {
            Page page = context.newPage();
            context.addInitScript( "Object.defineProperty(navigator, 'webdriver', { get: () => false })");

            page.navigate(String.valueOf(webScrapeRequest.url()),
                    new Page.NavigateOptions()
                            .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                            .setTimeout(webScrapeRequest.timeout().toMillis()));

            String url = webScrapeRequest.url().toString();
            //if (getIsPostOrPhoto(url)) {
                navigateToPost(page, url);
           // }
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
            page.waitForTimeout(3000);
            Locator dialog = page.locator("div[role='dialog']");
            if (dialog.count() > 0) {
                page.evaluate(
                        "document.querySelectorAll(\"div[role='dialog']\").forEach(e => e.remove());"
                );
                page.evaluate("document.body.style.overflow='auto'");
            }
        } catch (Exception e) {
            // Ignorar si el botón no se encuentra (el modal no apareció)
        }

        // Esperar un momento para visualizar la página cargada
        page.waitForTimeout(5000);
    }

    public static void main(String[] args) {
        try {
            DynamicWebScrape dynamicWebScrape = new DynamicWebScrape();
            String urlString = "https://m.facebook.com/MemeroMaestro/posts/wtf-facebook-lo-predijo/3811216802320206/";

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
