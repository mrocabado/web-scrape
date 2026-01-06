package com.mindwaresrl.core;


import com.microsoft.playwright.*;

public class BrowserManager {
    private static BrowserManager instance;
    private Playwright playwright;
    private Browser browser;

    private BrowserManager(){}

    public static synchronized BrowserManager getInstance(){
        if(instance==null){
            instance = new BrowserManager();

        }
        return instance;
    }
    public Browser start(boolean headless){
        if(playwright==null){
            playwright = Playwright.create();
            browser = playwright.chromium().launch(
              new BrowserType.LaunchOptions()
                      .setHeadless(headless)
                      .setArgs(
                              java.util.List.of(
                                      "--no-sandbox",
                              "--disable-setuid-sandbox",
                                      "--disable-gpu",
                                      "--disable-software-rasterizer"
                                      ))
            );
        }
        return browser;
    }

    public void close(){
       if(browser!=null)browser.close();
       if(playwright   !=null)playwright.close();
    }


}
