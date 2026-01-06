package com.mindwaresrl.orchestrator;

import com.mindwaresrl.core.*;
import com.mindwaresrl.extractors.*;
import com.mindwaresrl.models.*;
import com.microsoft.playwright.*;

import java.nio.file.Path;
import java.util.Optional;


import java.nio.file.Paths;

public class ScraperOrchestrator{
    private final BrowserManager browserManager;
    private final ContextFactory contextFactory;
    private final TextExtractor textExtractor;
    private final LinkExtractor linkExtractor;

    public ScraperOrchestrator() {
        this.browserManager = BrowserManager.getInstance();
        this.contextFactory = new ContextFactory();
        this.textExtractor = new TextExtractor();
        this.linkExtractor = new LinkExtractor();
    }

    public ScrapeResult scrape(String url, String strategy) {
        var browser = browserManager.start(true);
        long startTime = System.currentTimeMillis();


        try (BrowserContext context = contextFactory.createContext(browser, strategy)){

            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));

            Page page = context.newPage();

            var navigator = new PageNavigator(page);
            var response = navigator.gotoURl(url,strategy);

            try {
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get("debug_screen.png"))
                        .setFullPage(false));
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo tomar la captura (no es crítico): " + e.getMessage());
            }

            java.nio.file.Files.writeString(
                    Paths.get("debug_source.html"),
                    page.content()
            );
            if (response == null) {
                return ScrapeResult.failure(url, "No response from server (Network Error)");
            }

            String html = page.content();
            String title = page.title();

            //var textExt = new TextExtractor();
            //var linkExt = new LinkExtractor();

            String markdownContent = textExtractor.extract(html);
            var links = linkExtractor.extract(page, url);
            var content = new Content(
                    title,
                    markdownContent,
                    links
            );

            var meta = new FetchMetadata(
                    response.status(),
                    System.currentTimeMillis() - startTime,
                    0,
                    strategy
            );
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("trace.zip")));

            return new ScrapeResult(
                    url,
                    true,
                    Optional.of(meta),
                    Optional.of(content),
                    Optional.empty()
            );

        } catch (PlaywrightException e) {
            return ScrapeResult.failure(url, "Playwright Error: " + e.getMessage());
        } catch (Exception e) {
            return ScrapeResult.failure(url, "General Error: " + e.getMessage());
        }
    }

    public void shutdown() {
        browserManager.close();
    }
}