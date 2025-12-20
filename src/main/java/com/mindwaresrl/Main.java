package com.mindwaresrl;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

public class Main {

    public static void main(String[] args)  {
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
}
