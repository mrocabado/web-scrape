package com.mindwaresrl.common;

import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlaywrightManager {
    private static Playwright playwright;
    private static Browser browser;

    static {
        try {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chromium"));
            // El Shutdown Hook también iría aquí
            Runtime.getRuntime().addShutdownHook(new Thread(PlaywrightManager::closeAll));
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error starting up Playwright: " + e.getMessage());
        }
    }

    public static Browser browser() {
        return browser;
    }

    private static synchronized void closeAll() {
        log.info("Closing Playwright and browser...");
        if (browser != null) {
            browser.close();
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}
