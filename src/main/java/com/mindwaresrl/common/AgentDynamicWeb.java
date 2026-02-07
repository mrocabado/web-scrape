package com.mindwaresrl.common;

import com.microsoft.playwright.Browser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AgentDynamicWeb {

    // Fallback list (Smart Chrome strategy)
    private static final List<String> FALLBACK_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0");

    private static final List<String> LIVE_AGENTS = new ArrayList<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    static {
        // Attempt to load from API on startup, fall back if fails
        if (!loadAgentsFromGitHub()) {
            LIVE_AGENTS.addAll(FALLBACK_AGENTS);
        }
    }

    /**
     * Creates a full Browser Profile (Optimized Single Attempt).
     * Includes consistent User-Agent, Viewport, Locale, and Timezone.
     */
    public static Browser.NewContextOptions createProfile() {
        String userAgent = getNextAgent();

        return new Browser.NewContextOptions()
                .setUserAgent(userAgent)
                .setViewportSize(1920, 1080)
                .setLocale("es-419") 
                .setTimezoneId("America/La_Paz")
                .setHasTouch(false)
                .setJavaScriptEnabled(true)
                .setIgnoreHTTPSErrors(true);
    }

    public static String getNextAgent() {
        int index = counter.getAndIncrement() % LIVE_AGENTS.size();
        return LIVE_AGENTS.get(index);
    }

    public static int getTotalAgents() {
        return LIVE_AGENTS.size();
    }

    private static boolean loadAgentsFromGitHub() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://raw.githubusercontent.com/jnrbsn/user-agents/main/user-agents.json"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                // Simple parsing without external libraries (JSON is a simple List<String>)
                String[] agents = json.replace("[", "").replace("]", "").split(",");

                for (String rawAgent : agents) {
                    String agent = rawAgent.trim().replace("\"", "");
                    // Filter for Smart Chrome consistency
                    if (!agent.isEmpty() && (agent.contains("Chrome") || agent.contains("Edg"))
                            && !agent.contains("Firefox") && !agent.contains("Safari/60")) {
                        LIVE_AGENTS.add(agent);
                    }
                }

                if (!LIVE_AGENTS.isEmpty()) {
                    System.out.println("Loaded " + LIVE_AGENTS.size() + " fresh agents from GitHub API.");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load agents from API: " + e.getMessage());
        }
        return false;
    }
}
