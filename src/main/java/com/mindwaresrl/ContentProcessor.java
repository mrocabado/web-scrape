package com.mindwaresrl;


import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;


public class ContentProcessor {
    private final FlexmarkHtmlConverter mdConverter;
    public ContentProcessor(){
        this.mdConverter =FlexmarkHtmlConverter.builder().build();
    }

    public String processHtmlToMarkdown(String rawHtml){
        if(rawHtml ==null || rawHtml.isEmpty()) return "";

        String cleanHtml =Jsoup.clean(rawHtml,Safelist.relaxed());
        return mdConverter.convert(cleanHtml);
    }

}
