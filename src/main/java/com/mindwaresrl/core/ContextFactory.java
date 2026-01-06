package com.mindwaresrl.core;

import com.microsoft.playwright.*;
import java.util.List;
import java.util.OptionalInt;

public class ContextFactory {
    public BrowserContext createContext(Browser browser,String strategy){
        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36...")
                .setViewportSize(1920,1080)
                .setLocale("es-BO")
                .setTimezoneId("America/La_Paz");


        BrowserContext context = browser.newContext(options);
        if("STATIC".equalsIgnoreCase(strategy)){
            context.route("**/*.{png,jpg,jpeg,gif,css,woff}", Route::abort);
        }
        context.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        return context;
    }
}
