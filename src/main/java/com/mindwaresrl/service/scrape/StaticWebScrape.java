package com.mindwaresrl.service.scrape;

import com.mindwaresrl.common.FakeUserAgent;
import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.io.IOException;
import java.time.Instant;

@Slf4j
public class StaticWebScrape implements WebScrape {

    @Override
    public WebScrapeResult execute(WebScrapeRequest webScrapeRequest) throws IOException {
        var response = Jsoup.connect(String.valueOf(webScrapeRequest.url()))
                .userAgent(FakeUserAgent.chrome())
                .method(Connection.Method.GET)
                .timeout((int) webScrapeRequest.timeout().toMillis())
                .execute();

        if (response.statusCode() != 200) {
            return WebScrapeResult.EMPTY_RESULT;
        }

        var htmlBody = response.body();
        //Clean HTML using jsoup (remove scripts, events, unsafe tags)
        Document cleanDocument = Jsoup.parse(Jsoup.clean(htmlBody, Safelist.relaxed()));
//        String cleanHtml = cleanDocument.body().html();
        String cleanHtml = Jsoup.clean(htmlBody, Safelist.relaxed());

        //Convert cleaned HTML → Markdown with Flexmark
        FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();
        var markdown = converter.convert(cleanHtml);

        return new WebScrapeResult(markdown, "title", Instant.now());
    }
}
