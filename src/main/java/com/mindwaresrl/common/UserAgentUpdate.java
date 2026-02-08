package com.mindwaresrl.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class UserAgentUpdate {

    private static final String URL = "https://www.useragentlist.net/";

    private static final String FALLBACK_DESKTOP =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private static final String FALLBACK_MOBILE =
            "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";

    public static String randomDesktop() {
        List<String> agents = safeFetchUserAgents().stream()
                .filter(UserAgentUpdate::isDesktop)
                .toList();

        return pickOrFallback(agents, FALLBACK_DESKTOP);
    }

    public static String randomMobile() {
        List<String> agents = safeFetchUserAgents().stream()
                .filter(UserAgentUpdate::isMobile)
                .toList();

        return pickOrFallback(agents, FALLBACK_MOBILE);
    }

    public static String randomAny() {
        List<String> agents = safeFetchUserAgents();
        return pickOrFallback(agents, FALLBACK_DESKTOP);
    }



    private static boolean isDesktop(String ua) {
        return ua.contains("Windows")
                || ua.contains("Macintosh")
                || ua.contains("X11")
                || ua.contains("Linux x86_64");
    }

    private static boolean isMobile(String ua) {
        return ua.contains("Android")
                || ua.contains("iPhone")
                || ua.contains("Mobile");
    }

    private static List<String> safeFetchUserAgents() {
        try {
            return fetchUserAgents();
        } catch (IOException e) {
            System.err.println("Error al conectar con la web: " + e.getMessage());
            return List.of();
        }
    }

    private static List<String> fetchUserAgents() throws IOException {
        Document doc = Jsoup.connect(URL)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        Elements elements = doc.select("pre.wp-block-code");

        return elements.stream()
                .map(e -> e.text().trim())
                .collect(Collectors.toList());
    }

    private static String pickOrFallback(List<String> agents, String fallback) {
        if (agents.isEmpty()) return fallback;
        return agents.get(new Random().nextInt(agents.size()));
    }
}
