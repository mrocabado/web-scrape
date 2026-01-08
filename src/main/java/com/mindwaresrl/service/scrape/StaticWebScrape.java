package com.mindwaresrl.service.scrape;

import com.mindwaresrl.common.Conversion;
import com.mindwaresrl.common.FakeUserAgent;
import com.mindwaresrl.model.WebScrapeRequest;
import com.mindwaresrl.model.WebScrapeResult;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

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

        return Conversion.toWebScrapeResult(response.body());
    }
}
