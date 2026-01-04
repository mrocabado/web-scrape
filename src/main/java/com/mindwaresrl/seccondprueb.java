package com.mindwaresrl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class seccondprueb {
    public static void main(String[] args) {
        try {
            // .connect() hace la petición GET
            // .userAgent() es VITAL para no ser bloqueado
            // .get() descarga y parsea el HTML
            Document doc = Jsoup.connect("https://books.toscrape.com/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(5000)
                    .get();

            System.out.println("Título de la web: " + doc.title());
            System.out.println("Título de la web: " );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
