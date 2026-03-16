// Photo_Facebook.java - REESTRUCTURADO
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
public class Photo_Facebook {

    // Almacena los datos capturados
    private final List<String> capturedBodies = new CopyOnWriteArrayList<>();

    public Photo_Facebook(Page page, String url) {
        System.out.println("--------- Entrando a scraping de foto ---------");

        page.onResponse(response -> interceptResponse(response));

        page.navigate(url);
        page.waitForTimeout(5000);

        trigger_face.removeDialogs(page);
        page.waitForTimeout(1000);

        extractPhotoReactions(page);

        extractComments(page);

        parseCapturedData();
    }

    // -------------------------------------------------------
    // Intercepta respuestas Ajax de Facebook
    // -------------------------------------------------------
    private void interceptResponse(Response response) {
        String url = response.url();
        int status = response.status();

        boolean isRelevant =
                (url.contains("api/graphql") ||
                        url.contains("ajax/bz") ||
                        url.contains("reaction/profile") ||
                        url.contains("ufi/reaction")) && status == 200;

        if (isRelevant) {
            try {
                String body = response.text();
                if (body.contains("feedback") ||
                        body.contains("comment") ||
                        body.contains("reaction") ||
                        body.contains("like_count")) {
                    capturedBodies.add(body);
                    System.out.println(">>> Capturado endpoint: " + url.substring(0, Math.min(url.length(), 80)));
                }
            } catch (Exception ignored) { }
        }
    }

    // -------------------------------------------------------
    // Extrae reacciones visibles en el DOM de la foto
    // -------------------------------------------------------
    private void extractPhotoReactions(Page page) {
        System.out.println("\n=== REACCIONES DE LA FOTO ===");

        try {
            page.waitForSelector("div[aria-label*='reacci']",
                    new Page.WaitForSelectorOptions().setTimeout(8000));
        } catch (Exception e) {
            System.out.println("No se encontró selector de reacciones, intentando fallback...");
        }

        Locator allLabels = page.locator("div[aria-label], span[aria-label]");
        int count = allLabels.count();

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < count; i++) {
            try {
                String label = allLabels.nth(i).getAttribute("aria-label");
                if (label != null && label.matches(".*\\d+.*") && !seen.contains(label)) {
                    seen.add(label);
                    System.out.println("  Reacción encontrada: " + label);
                }
            } catch (Exception ignored) { }
        }

        Locator reactionSummary = page.locator("span:has-text('reaccion'), span:has-text('Me gusta')");
        for (int i = 0; i < Math.min(reactionSummary.count(), 5); i++) {
            try {
                String text = reactionSummary.nth(i).innerText();
                if (!text.isBlank()) System.out.println("  Summary: " + text);
            } catch (Exception ignored) { }
        }
    }

    private void extractComments(Page page) {
        System.out.println("\n=== COMENTARIOS ===");
        expandComments(page);

        Locator comments = page.locator("div[role='article']");
        int commentCount = comments.count();
        System.out.println("  Comentarios encontrados en DOM: " + commentCount);

        for (int i = 0; i < commentCount; i++) {
            try {
                Locator comment = comments.nth(i);
                String text = comment.innerText();

                if (text != null && text.length() > 3 && text.length() < 2000) {
                    System.out.println("\n  --- Comentario " + (i + 1) + " ---");
                    System.out.println("  " + text.replaceAll("\n", " | ").substring(0, Math.min(text.length(), 200)));

                    // Buscar reacciones dentro del comentario
                    Locator commentReactions = comment.locator("span[aria-label], div[aria-label]");
                    for (int j = 0; j < commentReactions.count(); j++) {
                        String reactionLabel = commentReactions.nth(j).getAttribute("aria-label");
                        if (reactionLabel != null && reactionLabel.matches(".*\\d+.*")) {
                            System.out.println("    Reacciones: " + reactionLabel);
                        }
                    }
                }
            } catch (Exception ignored) { }
        }
    }

    // -------------------------------------------------------
    // Click en "Ver más comentarios"
    // -------------------------------------------------------
    //corregir
    private void expandComments(Page page) {
        try {
            // Buscar botón "Ver más comentarios" o "Ver X comentarios"
            Locator moreBtn = page.locator(
                    "div[role='button']:has-text('Ver más comentarios'), " +
                            "div[role='button']:has-text('Ver comentarios'), " +
                            "span:has-text('Ver más comentarios')"
            );
            if (moreBtn.count() > 0) {
                moreBtn.first().click();
                page.waitForTimeout(3000);
                System.out.println("  >>> Click en 'Ver más comentarios'");
            }
        } catch (Exception e) {
            System.out.println("  (No hay botón de más comentarios)");
        }
    }

    // -------------------------------------------------------
    // Parsea los bodies Ajax capturados buscando datos clave
    // -------------------------------------------------------
    private void parseCapturedData() {
        System.out.println("\n=== DATOS AJAX CAPTURADOS (" + capturedBodies.size() + " responses) ===");

        for (String body : capturedBodies) {
            String cleanBody = body.startsWith("for (;;);") ? body.substring(9) : body;

            extractJsonValue(cleanBody, "like_count");
            extractJsonValue(cleanBody, "comment_count");
            extractJsonValue(cleanBody, "reaction_count");
            extractJsonValue(cleanBody, "share_count");

            Pattern reactionPattern = Pattern.compile("\"reaction_type\":\"(\\w+)\".*?\"count\":(\\d+)");
            Matcher m = reactionPattern.matcher(cleanBody);
            while (m.find()) {
                System.out.println("  Tipo reacción: " + m.group(1) + " -> " + m.group(2));
            }
        }
    }

    private void extractJsonValue(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\":\\{?\"?count\"?:(\\d+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            System.out.println("  " + key + ": " + m.group(1));
        }
    }
}