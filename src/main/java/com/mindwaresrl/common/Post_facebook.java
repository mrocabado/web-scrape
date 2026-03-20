package com.mindwaresrl.common;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Post_facebook {

    private final List<String> capturedBodies = new CopyOnWriteArrayList<>();

    public Post_facebook(Page page, String url) {
        System.out.println("--------- Iniciando Scraping de POST ---------");

        // 1. Escuchar respuestas ANTES de navegar
        page.onResponse(this::interceptResponse);

        // 2. Navegar y limpiar UI
        page.navigate(url);
        page.waitForTimeout(4000);
        trigger_face.removeDialogs(page); 

        extractMainContent(page);

        // 4. Forzar carga de datos (Scroll + Expandir)
        scrollAndDiscover(page);
        expandCommentsLoop(page);

        // 5. Extraer datos del DOM
        extractReactionsFromDOM(page);
        extractCommentsFromDOM(page);

        // 6. Procesar lo capturado por AJAX
        parseCapturedData();
    }

    private void interceptResponse(Response response) {
        String url = response.url();
        if ((url.contains("api/graphql") || url.contains("ajax/bz")) && response.status() == 200) {
            try {
                String body = response.text();
                // Filtramos por palabras clave de posts y comentarios
                if (body.contains("feedback") || body.contains("node") || body.contains("comment")) {
                    capturedBodies.add(body);
                }
            } catch (Exception ignored) {}
        }
    }

    private void extractMainContent(Page page) {
        System.out.println("\n=== CONTENIDO DEL POST ===");
        try {
            // Selectores comunes para el texto de la publicación
            Locator postText = page.locator("div[data-ad-comet-preview='message'], div[role='main'] div[dir='auto']");
            if (postText.first().isVisible()) {
                System.out.println("Texto: " + postText.first().innerText());
            }
        } catch (Exception e) {
            System.out.println("No se pudo extraer el texto principal.");
        }
    }

    private void scrollAndDiscover(Page page) {
        // Hacemos un scroll pequeño para activar los triggers de carga de Facebook
        page.evaluate("window.scrollBy(0, 500)");
        page.waitForTimeout(2000);
    }

    private void expandCommentsLoop(Page page) {
        System.out.println("\n=== EXPANDIENDO COMENTARIOS ===");
        int clics = 0;
        while (clics < 5) { // Límite de seguridad
            Locator moreBtn = page.locator("span:has-text('Ver más comentarios'), span:has-text('Ver más respuestas'), div[role='button']:has-text('comentarios')").first();
            if (moreBtn.isVisible()) {
                moreBtn.click();
                page.waitForTimeout(2500);
                clics++;
            } else {
                break;
            }
        }
        System.out.println("Se hicieron " + clics + " expansiones.");
    }

    private void extractReactionsFromDOM(Page page) {
        System.out.println("\n=== REACCIONES (DOM) ===");
        // En posts, las reacciones suelen estar en un contenedor con aria-label
        Locator reactions = page.locator("span[data-ad-comet-preview='footer_reaction_count'], span[aria-label*='reacc']");
        for (int i = 0; i < reactions.count(); i++) {
            String label = reactions.nth(i).getAttribute("aria-label");
            if (label != null) System.out.println("  - " + label);
        }
    }

    private void extractCommentsFromDOM(Page page) {
        System.out.println("\n=== COMENTARIOS (DOM) ===");
        Locator comments = page.locator("div[role='article']");
        for (int i = 0; i < Math.min(comments.count(), 20); i++) {
            try {
                String text = comments.nth(i).innerText();
                if (!text.isBlank()) {
                    System.out.println("Comentario " + (i+1) + ": " + text.replace("\n", " ").substring(0, Math.min(text.length(), 100)) + "...");
                }
            } catch (Exception ignored) {}
        }
    }

    private void parseCapturedData() {
        System.out.println("\n=== ANÁLISIS AJAX (" + capturedBodies.size() + " respuestas) ===");
        for (String body : capturedBodies) {
            String clean = body.startsWith("for (;;);") ? body.substring(9) : body;
            
            // Buscar conteos globales en el JSON
            checkRegex(clean, "comet_sections.*?action_count\":(\\d+)", "Likes/Reacciones");
            checkRegex(clean, "comment_count\":.*?count\":(\\d+)", "Total Comentarios");
            checkRegex(clean, "share_count\":.*?count\":(\\d+)", "Total Shares");
        }
    }

    private void checkRegex(String json, String pattern, String label) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(json);
        if (m.find()) {
            System.out.println("  [AJAX] " + label + ": " + m.group(1));
        }
    }
}