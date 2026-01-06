package com.mindwaresrl.extractors;
import org.jsoup.Jsoup;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import org.jsoup.nodes.Document;
/*
public class TextExtractor {
    public String extract(String html){
        Document doc = Jsoup.parse(html);
        doc.select("script, style, nav, footer, iframe, .ad-banner").remove();
        String text = doc.body().text();
        return text.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .reduce((a,b)->a+"\n"+b)
                .orElse("");
    }
}
*/
public class TextExtractor {
    private final FlexmarkHtmlConverter converter;
    public TextExtractor() {
        this.converter = FlexmarkHtmlConverter.builder().build();
    }
    public String extract(String rawHtml) {
        if (rawHtml == null || rawHtml.isEmpty()) return "";
        //Document doc = Jsoup.parse(rawHtml);
        //doc.select("script, style, iframe, nav, footer, header, .ad, .cookie-banner").remove();
//String cleanHtml = Jsoup.clean(ranHtml,Safelist.relaxed());
//        FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();
//        return converter.convert(cleanHtml);
        // String cleanedHtmlForMarkdown = doc.body().html();
        String cleanHtml = Jsoup.clean(rawHtml,Safelist.relaxed());
        return converter.convert(cleanHtml);
    }
}