package com.mindwaresrl.common;

import com.mindwaresrl.model.WebScrapeResult;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.time.Instant;

public class Conversion {

    public static WebScrapeResult toWebScrapeResult(String htmlContent) {
        var document = Jsoup.parse(htmlContent);
        var title = document.title();
        var bodyHtml = document.body().html();

        //Clean HTML using jsoup (remove scripts, events, unsafe tags)
        String cleanBodyHtml = Jsoup.clean(bodyHtml, Safelist.relaxed());

        //Convert cleaned HTML → Markdown with Flexmark
        FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();
        var markdown = converter.convert(cleanBodyHtml);

        return new WebScrapeResult(markdown, title, Instant.now());
    }
}
