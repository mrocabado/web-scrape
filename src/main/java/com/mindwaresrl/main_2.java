package com.mindwaresrl;

import com.mindwaresrl.orchestrator.*;
import com.mindwaresrl.models.*;


public class main_2 {

    public static void main(String[] args) {
        ScraperOrchestrator orchestrator = new ScraperOrchestrator();

        System.out.println("Iniciando scraping...");
        //"STATIC" solo agarra el texto sin imagenes
        //"DYNAMIC" obtiene imagenes, scroll en la pagina

        ScrapeResult result = orchestrator.scrape("https://es.wikipedia.org/wiki/Bolivia", "STATIC");

        if (result.success()) {
            Content c = result.content().get();
            FetchMetadata m = result.metadata().get();

            System.out.println("✅ Título: " + c.title());
            System.out.println("⏱️ Tiempo: " + m.loadTimeMs() + "ms");
            System.out.println("📄 Texto (extracto): " + c.cleanText().substring(0, Math.min(c.cleanText().length(), 100)) + "...");
        } else {
            System.err.println("❌ Error: " + result.error().get());
        }

        orchestrator.shutdown();
    }
}
