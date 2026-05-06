package com.mindwaresrl.common.facebook;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.mindwaresrl.model.facebook.ReactionData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReactionExtractor {

    public List<ReactionData> extract(Page page) {
        System.out.println("\n========== [REACCIONES] INICIO ==========");

        Locator labels = page.locator(FacebookSelectors.POST_REACTIONS_LABEL);
        int count = labels.count();
        System.out.println("[REACCIONES] aria-labels totales en página: " + count);

        Set<String> seen = new HashSet<>();
        List<ReactionData> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String label = readAriaLabel(labels.nth(i));
            if (!isValidPostReaction(label, seen)) continue;
            seen.add(label);
            ReactionData r = parseReaction(label);
            System.out.println("  ✓ " + r);
            out.add(r);
        }

        System.out.println("[REACCIONES] Total: " + out.size());
        System.out.println("========== [REACCIONES] FIN ==========\n");
        return out;
    }

    private Locator resolveScope(Page page) {
        Locator main = page.locator(FacebookSelectors.POST_SCOPE);
        if (main.count() > 0) {
            System.out.println("[REACCIONES] Usando scope acotado al post");
            return main.first();
        }
        System.out.println("[REACCIONES] WARN: no se encontró scope, usando body completo");
        return page.locator("body");
    }

    private List<ReactionData> extractFromScope(Locator scope) {
        Locator labels = scope.locator(FacebookSelectors.POST_REACTIONS_LABEL);
        int count = labels.count();
        System.out.println("[REACCIONES] aria-labels candidatos en scope: " + count);

        Set<String> seen = new HashSet<>();
        List<ReactionData> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String label = readAriaLabel(labels.nth(i));
            if (label == null) continue;

            System.out.println("  candidato[" + i + "]: '" + truncate(label, 80) + "'");
            if (!isValidPostReaction(label, seen)) {
                System.out.println("     ✗ descartado (no es reacción de post)");
                continue;
            }
            seen.add(label);
            ReactionData r = parseReaction(label);
            System.out.println("     ✓ aceptado -> " + r);
            out.add(r);
        }
        return out;
    }

    private String readAriaLabel(Locator l) {
        try { return l.getAttribute("aria-label"); }
        catch (Exception e) { return null; }
    }

    private boolean isValidPostReaction(String label, Set<String> seen) {
        return label != null
                && label.matches(FacebookSelectors.POST_REACTION_REGEX)
                && !seen.contains(label);
    }

    private ReactionData parseReaction(String label) {
        // "Me gusta: 793 personas" -> type="Me gusta", count="793"
        String[] parts = label.split(":", 2);
        String type = parts[0].trim();
        String countStr = parts.length > 1 ? parts[1].replaceAll("\\D+", "") : "";
        return new ReactionData(type, "dom-post", countStr);
    }

    private String truncate(String s, int max) {
        return s == null ? "null" : s.substring(0, Math.min(s.length(), max));
    }
}