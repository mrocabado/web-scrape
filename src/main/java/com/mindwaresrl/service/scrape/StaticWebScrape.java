package com.mindwaresrl.service.scrape;

import com.mindwaresrl.common.FakeUserAgent;
import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.IOException;
import java.time.Instant;

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

        var responseHtml = response.body();
        var document = Jsoup.parse(responseHtml);
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
