package com.mindwaresrl;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

public class Main {

    public static void main(String[] args)  {
        htmlToMarkdown();
        getPageTitleWithPlayWright();
    }

    private static void htmlToMarkdown() {
        // Raw HTML that may be messy or contain unsafe tags
        String rawHtml = """
            <html>
                <head><title>Example</title></head>
                <body>
                    <h1>Hello World</h1>
                    <p>This is <b>bold</b> and <i>italic</i>.</p>
                    <script>alert('hack');</script>
                    <a href="https://example.com">Link!</a>
                    <ul><li>Item A</li><li>Item B</li></ul>
                </body>
            </html>
        """;

        // 1️⃣ Clean HTML using jsoup (remove scripts, events, unsafe tags)
        Document cleanDocument = Jsoup.parse(Jsoup.clean(rawHtml, Safelist.relaxed()));

        String cleanHtml = cleanDocument.toString();

        // 2️⃣ Convert cleaned HTML → Markdown with Flexmark
        FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();
        String markdown = converter.convert(cleanHtml);

        // Print Markdown output
        System.out.println("=== MARKDOWN OUTPUT ===");
        System.out.println(markdown);
    }

    private static void getPageTitleWithPlayWright() {
        System.out.println("Getting title with Playwright");

        try(var pw = Playwright.create(); var browser =  pw.chromium().launch(new BrowserType.LaunchOptions().setChannel("chromium"))) {
            var context = browser.newContext();
            var page = context.newPage();
            page.navigate("https://playwright.dev/java/");
            String title = page.title();
            System.out.println("Page Title: " + title);
    }

        System.out.println("Getting title with Playwright");

        try(var pw = Playwright.create(); var browser =  pw.chromium().launch(new BrowserType.LaunchOptions().setChannel("chromium"))) {
            var context = browser.newContext();
            var page = context.newPage();
            page.navigate("http://127.0.0.1:8000/7%20Awesome%20Libraries%20for%20Java%20Unit%20and%20Integration%20Testing.html");
            String title = page.title();
            System.out.println("Page Title: " + title);
        }
     }
}
