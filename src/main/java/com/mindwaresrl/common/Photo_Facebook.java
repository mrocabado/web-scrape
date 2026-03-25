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
        log.info("=== COMENTARIOS ===");
        expandComments(page);
        printAllComments(page);
    }


    private void expandComments(Page page) {
        try {
            loadAllComments(page);
            expandCommentReplies(page);
        } catch (Exception e) {
            log.info("  (No hay botones de comentarios para expandir)");
        }
    }


    private void loadAllComments(Page page) {
        int maxClicks = 20;
        int clicks = 0;

        while (clicks < maxClicks) {
            Locator moreBtn = page.locator(
                    "div[role='button']:has-text('Ver más comentarios')"
            );
            if (moreBtn.count() == 0) break;

            moreBtn.first().click();
            log.info("  >>> Click #{} en 'Ver más comentarios'", ++clicks);
            page.waitForTimeout(2500); // esperar que cargue el siguiente batch
        }

        log.info("  Total clicks 'Ver más': {}", clicks);
    }

    private void expandCommentReplies(Page page) {
        Locator replyBtns = page.locator("div[role='button']:has-text('respuesta')");
        int count = replyBtns.count();
        log.info("  Botones 'Ver respuestas' encontrados: {}", count);

        for (int i = 0; i < count; i++) {
            clickReplyButton(replyBtns.nth(i));
        }
    }


    private void clickReplyButton(Locator btn) {
        try {
            String text = btn.innerText().trim();

            if (text.matches("Ver (las )?\\d+ respuesta(s)?")) {
                btn.click();
                log.info("  >>> Click en: {}", text);
                btn.page().waitForTimeout(1500);
            }
        } catch (Exception ignored) { }
    }


    private void printAllComments(Page page) {
        Locator comments = page.locator("div[role='article']");
        int total = comments.count();
        log.info("  Comentarios en DOM: {}", total);

        for (int i = 0; i < total; i++) {
            printSingleComment(comments.nth(i), i + 1);
        }
    }


    private void printSingleComment(Locator comment, int index) {
        try {
            String author = extractCommentAuthor(comment);
            String text   = extractCommentText(comment);
            String time   = extractCommentTime(comment);
            String reacts = extractCommentReactionCount(comment);

            if (!isValidCommentText(text)) return;

            log.info("\n  --- Comentario {} ---", index);
            log.info("  Autor   : {}", author);
            log.info("  Texto   : {}", truncate(text, 300));
            log.info("  Tiempo  : {}", time);
            log.info("  Reacciones: {}", reacts);

        } catch (Exception ignored) { }
    }

// -------------------------------------------------------
// Sub-selectores dentro de un comentario
// -------------------------------------------------------

    // El nombre del autor está en el primer <a> con role="link" dentro del artículo
// Complejidad: 2
    private String extractCommentAuthor(Locator comment) {
        try {
            // Facebook pone el nombre en un <a> con aria-label o simplemente texto
            Locator authorLink = comment.locator("a[role='link']").first();
            return authorLink.innerText().trim();
        } catch (Exception e) {
            return "(sin autor)";
        }
    }

    // El texto real del comentario está en un <div dir="auto"> que NO sea el nombre
// Complejidad: 2
    private String extractCommentText(Locator comment) {
        try {
            // div[dir='auto'] dentro del comentario — el primero suele ser el texto
            // Filtramos los que son solo espacios o muy cortos (fechas, etc.)
            Locator textDivs = comment.locator("div[dir='auto']");
            for (int i = 0; i < textDivs.count(); i++) {
                String t = textDivs.nth(i).innerText().trim();
                if (t.length() > 5) return t; // el primero válido es el texto
            }
            return "(sin texto)";
        } catch (Exception e) {
            return "(sin texto)";
        }
    }

    // La fecha/hora relativa está en un <a> con href que contiene "comment_id"
// Complejidad: 2
    private String extractCommentTime(Locator comment) {
        try {
            Locator timeLink = comment.locator("a[href*='comment_id']").first();
            return timeLink.innerText().trim();
        } catch (Exception e) {
            return "(sin fecha)";
        }
    }

    // Las reacciones del comentario: número en un span aria-label con "reacci"
// Complejidad: 2
    private String extractCommentReactionCount(Locator comment) {
        try {
            // Facebook renderiza algo como aria-label="16 reacciones, mira quién..."
            Locator reactionEl = comment.locator("span[aria-label*='reacci'], div[aria-label*='reacci']").first();
            return reactionEl.getAttribute("aria-label");
        } catch (Exception e) {
            // Fallback: buscar span con solo número (ej: "16", "34")
            return extractNumericReaction(comment);
        }
    }

    // Complejidad: 3
    private String extractNumericReaction(Locator comment) {
        try {
            Locator spans = comment.locator("span");
            for (int i = 0; i < spans.count(); i++) {
                String t = spans.nth(i).innerText().trim();
                if (t.matches("^\\d+$")) return t + " reacciones";
            }
            return "0";
        } catch (Exception e) {
            return "0";
        }
    }

// -------------------------------------------------------
// Utilidades
// -------------------------------------------------------

    private boolean isValidCommentText(String text) {
        return text != null && text.length() > 3;
    }

    private String truncate(String text, int max) {
        return text.substring(0, Math.min(text.length(), max));
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