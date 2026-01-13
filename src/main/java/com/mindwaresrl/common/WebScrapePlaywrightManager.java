package com.mindwaresrl.common;

import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebScrapePlaywrightManager {
    private static Playwright playwright;
    private static Browser browser;

    static {
        try {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chromium"));
            // El Shutdown Hook también iría aquí
            Runtime.getRuntime().addShutdownHook(new Thread(WebScrapePlaywrightManager::closeAll));
            System.out.println("WebScrape Playwright process and browser started...");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error starting up WebScrape Playwright: " + e.getMessage());
        }
    }

    public static Browser browser() {
        return browser;
    }

    public static synchronized void closeAll() {
        System.out.println("Closing WebScrape Playwright and browser...");

        try {
            if (browser != null) {
                // Intentamos cerrar, pero ignoramos si el pipe ya se cerró
                browser.close();
                log.info("WebScrape Browser closed");
            }
        } catch (PlaywrightException e) {
            if (e.getMessage().contains("pipe closed") || e.getMessage().contains("Target closed")) {
                log.warn("WebScrape Browser closed by the OS (Pipe closed)");
            } else {
                log.error("Unexpected error when closing the WebScrape Browser", e);
            }
        } finally {
            browser = null;
        }

        try {
            if (playwright != null) {
                playwright.close();
                log.info("WebScrape Playwright instance released.");
            }
        } catch (Exception e) {
            // En un shutdown hook, ser permisivo es mejor que lanzar excepciones
            log.warn("Nota: WebScrape Playwright instance could not be released (may be already closed).");
        } finally {
            playwright = null;
        }
    }
}
