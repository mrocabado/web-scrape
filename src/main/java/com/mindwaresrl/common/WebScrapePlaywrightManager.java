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
            System.out.println("Playwright process and browser started...");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error starting up Playwright: " + e.getMessage());
        }
    }

    public static Browser browser() {
        return browser;
    }

    public static synchronized void closeAll() {
        System.out.println("Closing Playwright and browser...");

        try {
            if (browser != null) {
                // Intentamos cerrar, pero ignoramos si el pipe ya se cerró
                browser.close();
                log.info("Browser closed");
            }
        } catch (PlaywrightException e) {
            if (e.getMessage().contains("pipe closed") || e.getMessage().contains("Target closed")) {
                log.warn("Browser closed by the OS (Pipe closed)");
            } else {
                log.error("Unexpected error when closing the Browser", e);
            }
        } finally {
            browser = null;
        }

        try {
            if (playwright != null) {
                playwright.close();
                log.info("Playwright instance released.");
            }
        } catch (Exception e) {
            // En un shutdown hook, ser permisivo es mejor que lanzar excepciones
            log.warn("Nota: Playwright instance could not be released (may be already closed).");
        } finally {
            playwright = null;
        }
    }
}
