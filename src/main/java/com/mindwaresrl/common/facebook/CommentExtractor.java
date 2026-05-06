package com.mindwaresrl.common.facebook;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.mindwaresrl.model.facebook.CommentData;

import java.util.ArrayList;
import java.util.List;

public class CommentExtractor {

    public List<CommentData> extract(Page page) {
        System.out.println("\n========== [COMENTARIOS] INICIO ==========");
        expandAll(page);
        List<CommentData> comments = readAll(page);
        System.out.println("[COMENTARIOS] Total: " + comments.size());
        for (int i = 0; i < comments.size(); i++) {
            System.out.println("  [" + i + "] " + comments.get(i));
        }
        System.out.println("========== [COMENTARIOS] FIN ==========\n");
        return comments;
    }

    // ---------- Expansión ----------
    private void expandAll(Page page) {
        try {
            loadMore(page);
            expandReplies(page);
        } catch (Exception e) {
            System.out.println("[COMENTARIOS] Error expandiendo: " + e.getMessage());
        }
    }

    private void loadMore(Page page) {
        for (int i = 0; i < FacebookSelectors.MAX_LOAD_CLICKS; i++) {
            if (!clickLoadMoreOnce(page, i + 1)) {
                System.out.println("[COMENTARIOS] No hay más botón 'Ver más comentarios' tras " + i + " clicks");
                return;
            }
        }
    }

    private boolean clickLoadMoreOnce(Page page, int attempt) {
        Locator btn = page.locator(FacebookSelectors.LOAD_MORE_COMMENTS);
        if (btn.count() == 0) return false;
        try {
            OverlayCleaner.purge(page);                            // <-- importante
            btn.first().click(new Locator.ClickOptions()
                    .setForce(true)                                // <-- bypass overlay
                    .setTimeout(5000));
            System.out.println("[COMENTARIOS] Click #" + attempt + " OK");
            page.waitForTimeout(FacebookSelectors.AFTER_LOAD_MS);
            return true;
        } catch (Exception e) {
            System.out.println("[COMENTARIOS] Click #" + attempt + " falló: " + brief(e));
            return false;
        }
    }
    private String brief(Exception e) {
        String m = e.getMessage();
        return m == null ? e.getClass().getSimpleName()
                : m.substring(0, Math.min(m.length(), 80));
    }

    // CommentExtractor.java

    private void expandReplies(Page page) {
        int round = 0;
        int safetyMax = 5;  // bajamos a 5 — si en 5 rondas no crece, no va a crecer
        while (round < safetyMax) {
            Locator buttons = page.locator(FacebookSelectors.SHOW_REPLIES);
            int count = buttons.count();
            if (count == 0) {
                System.out.println("[COMENTARIOS] No quedan botones 'Ver respuestas'");
                return;
            }
            System.out.println("[COMENTARIOS] Ronda " + (round + 1)
                    + ": " + count + " botones de respuesta");

            int beforeArticles = page.locator(FacebookSelectors.COMMENT_ARTICLE).count();

            for (int i = 0; i < count; i++) {
                tryClickReply(buttons.nth(i), i);
            }

            // Esperamos crecimiento real del DOM
            boolean grew = waitForArticleGrowth(page, beforeArticles, 4000);

            // Detectamos si FB intentó mostrar el login después del click
            if (loginDialogAppeared(page)) {
                System.out.println("[COMENTARIOS] ⚠ FB pidió login al expandir respuestas. " +
                        "En logged-out no se pueden cargar más comentarios/respuestas.");
                return;
            }

            if (!grew) {
                System.out.println("[COMENTARIOS] DOM no creció esta ronda, abandono");
                return;
            }
            round++;
        }
    }

    private void tryClickReply(Locator btn, int idx) {
        try {
            // Chequeo rápido: ¿el botón sigue presente y visible?
            if (btn.count() == 0) {
                return;  // ya no existe, lo saltamos en silencio
            }
            String text;
            try {
                text = btn.innerText(new Locator.InnerTextOptions().setTimeout(2000)).trim();
            } catch (Exception e) {
                return;  // botón ya desapareció
            }
            if (!text.matches(FacebookSelectors.REPLY_TEXT_REGEX)) {
                return;
            }

            btn.scrollIntoViewIfNeeded(new Locator.ScrollIntoViewIfNeededOptions().setTimeout(2000));
            btn.page().waitForTimeout(150);
            OverlayCleaner.purge(btn.page());

            btn.evaluate("el => el.click()");
            System.out.println("[COMENTARIOS] reply[" + idx + "] dispatch click: '" + text + "'");
            btn.page().waitForTimeout(FacebookSelectors.AFTER_REPLY_MS);
        } catch (Exception e) {
            // botón obsoleto u otro error, no es crítico
            System.out.println("[COMENTARIOS] reply[" + idx + "] saltado: " + brief(e));
        }
    }

    private boolean waitForArticleGrowth(Page page, int previousCount, int maxWaitMs) {
        long deadline = System.currentTimeMillis() + maxWaitMs;
        while (System.currentTimeMillis() < deadline) {
            int now = page.locator(FacebookSelectors.COMMENT_ARTICLE).count();
            if (now > previousCount) {
                System.out.println("[COMENTARIOS] articles: " + previousCount + " -> " + now);
                page.waitForTimeout(500);
                return true;
            }
            page.waitForTimeout(200);
        }
        System.out.println("[COMENTARIOS] timeout esperando crecimiento (sigo igual: "
                + previousCount + ")");
        return false;
    }

    /**
     * Detecta si FB intentó abrir el dialog de "Iniciar sesión" como respuesta al click.
     * En la versión logged-out, FB bloquea expandir respuestas y muestra este popup.
     */
    private boolean loginDialogAppeared(Page page) {
        try {
            Object res = page.evaluate("""
            () => {
              const dialogs = document.querySelectorAll('[role="dialog"]');
              for (const d of dialogs) {
                const t = (d.innerText || '').toLowerCase();
                if (t.includes('iniciar sesión') || t.includes('log in') || t.includes('contraseña')) {
                  return true;
                }
              }
              // También buscamos forms de login flotantes recién aparecidos
              const forms = document.querySelectorAll('form');
              for (const f of forms) {
                if (f.querySelector('input[name="email"]') || f.querySelector('input[name="pass"]')) {
                  return true;
                }
              }
              return false;
            }
        """);
            return Boolean.TRUE.equals(res);
        } catch (Exception e) {
            return false;
        }
    }
    private List<CommentData> readAll(Page page) {
        Locator comments = page.locator(FacebookSelectors.COMMENT_ARTICLE);
        int total = comments.count();
        System.out.println("[COMENTARIOS] Articles encontrados: " + total);

        List<CommentData> out = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            CommentData c = parse(comments.nth(i), i);
            if (c != null) out.add(c);
        }
        return out;
    }

    private CommentData parse(Locator comment, int idx) {
        try {
            String author = readAuthor(comment);
            String text   = readText(comment, author);
            String time   = readTime(comment);
            String reacts = readReactionCount(comment);
            boolean isReply = isReplyArticle(comment);

            System.out.println("[COMENTARIOS] parse[" + idx + "]: "
                    + (isReply ? "[reply] " : "[comment] ")
                    + "author='" + author
                    + "' | text='" + truncate(text, 60) + "' | time='" + time
                    + "' | reacts='" + truncate(reacts, 30) + "'");

            return new CommentData(author, truncate(text, 300), time, reacts, isReply);
        } catch (Exception e) {
            System.out.println("[COMENTARIOS] parse[" + idx + "] error: " + e.getMessage());
            return null;
        }
    }

    private boolean isReplyArticle(Locator comment) {
        try {
            String label = comment.getAttribute("aria-label");
            return label != null && label.toLowerCase().startsWith("respuesta");
        } catch (Exception e) {
            return false;
        }
    }


    private void dumpStructure(Locator comment, int idx) {
        try {
            Object res = comment.evaluate("""
            el => {
              const links = [...el.querySelectorAll('a')].map((a, i) => ({
                i,
                text: (a.innerText || '').trim().slice(0, 50),
                href: (a.href || '').slice(0, 80),
                ariaLabel: (a.getAttribute('aria-label') || '').slice(0, 50)
              }));
              const divs = [...el.querySelectorAll('div[dir="auto"]')].map((d, i) => ({
                i,
                text: (d.innerText || '').trim().slice(0, 50),
                inAnchor: !!d.closest('a')
              }));
              return JSON.stringify({ links, divs }, null, 2);
            }
        """);
            System.out.println("[DEBUG][" + idx + "] estructura:\n" + res);
        } catch (Exception e) {
            System.out.println("[DEBUG][" + idx + "] dump fallo: " + e.getMessage());
        }
    }


    private String readAuthor(Locator comment) {
        // Estrategia 1: aria-label del article
        String fromAria = readAuthorFromAriaLabel(comment);
        if (fromAria != null) return fromAria;

        // Estrategia 2: fallback a links con texto
        return readAuthorFromLinks(comment);
    }

    private String readAuthorFromAriaLabel(Locator comment) {
        try {
            String label = comment.getAttribute("aria-label");
            if (label == null) return null;
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("(?:Comentario|Respuesta)\\s+de\\s+(.+?)\\s+hace\\s+",
                            java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(label);
            if (m.find()) return m.group(1).trim();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String readAuthorFromLinks(Locator comment) {
        try {
            Object res = comment.evaluate("""
            el => {
              const blocked = new Set(['GIPHY','GIF','Tenor','YouTube','Spotify']);
              const links = el.querySelectorAll('a');
              for (const l of links) {
                const t = (l.innerText || '').trim();
                if (t.length < 2) continue;
                if (blocked.has(t)) continue;
                if (/^\\d+\\s*(sem|min|h|d|s|sg|seg)/i.test(t)) continue;
                const href = l.getAttribute('href') || '';
                if (href === '#' || href === '') continue;
                if (t.length > 100) continue;
                return t.split('\\n')[0].trim();
              }
              return null;
            }
        """);
            return res == null ? "(sin autor)" : res.toString();
        } catch (Exception e) {
            return "(sin autor)";
        }
    }
    private String readText(Locator comment, String author) {
        try {
            Object res = comment.evaluate("""
            (el, authorName) => {
              const out = [];
              const divs = el.querySelectorAll('div[dir="auto"]');
              for (const d of divs) {
                if (d.closest('a')) continue;
                const t = (d.innerText || '').trim();
                if (t.length < 2) continue;
                if (authorName && t === authorName) continue;
                if (/^\\d+\\s*(sem|min|h|d|s|sg|seg)$/i.test(t)) continue;
                out.push(t);
              }
              return out.join('\\n');
            }
        """, author);
            String s = res == null ? "" : res.toString();
            return s.isEmpty() ? "(sin texto)" : s;
        } catch (Exception e) {
            return "(sin texto)";
        }
    }

    /**
     * Tiempo: buscamos en TODO el texto del comentario el patrón "N sem", "N min", etc.
     * No nos atamos a un selector específico.
     */
    private String readTime(Locator comment) {
        try {
            Object res = comment.evaluate("""
            el => {
              const text = el.innerText || '';
              const m = text.match(/(\\d+)\\s*(sem|min|h|d|sg|seg|s)\\b/i);
              return m ? m[0] : null;
            }
        """);
            return res == null ? "(sin fecha)" : res.toString();
        } catch (Exception e) {
            return "(sin fecha)";
        }
    }

    private String readReactionCount(Locator comment) {
        try {
            String label = comment.locator(FacebookSelectors.COMMENT_REACTIONS)
                    .first().getAttribute("aria-label");
            if (label == null) return "0";
            // Extraemos solo el número de la etiqueta
            String num = label.replaceAll("\\D+", "");
            return num.isEmpty() ? label : num;
        } catch (Exception e) {
            return "0";
        }
    }

    private String safeText(Locator l) {
        try { return l.innerText().trim(); } catch (Exception e) { return ""; }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.substring(0, Math.min(s.length(), max));
    }
}