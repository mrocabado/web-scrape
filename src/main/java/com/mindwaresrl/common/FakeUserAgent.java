package com.mindwaresrl.common;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

//TODO update user agents periodically
public class FakeUserAgent {
    private static final Random random = new Random();

    // Updated user agents as of 2025
    private static final List<String> CHROME_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

    private static final List<String> FIREFOX_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:121.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (X11; Linux x86_64; rv:121.0) Gecko/20100101 Firefox/121.0");

    private static final List<String> SAFARI_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1");

    private static final List<String> EDGE_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0");

    /**
     * Get a random Chrome user agent
     */
    public static String chrome() {
        return CHROME_AGENTS.get(random.nextInt(CHROME_AGENTS.size()));
    }

    /**
     * Get a random Firefox user agent
     */
    public static String firefox() {
        return FIREFOX_AGENTS.get(random.nextInt(FIREFOX_AGENTS.size()));
    }

    /**
     * Get a random Safari user agent
     */
    public static String safari() {
        return SAFARI_AGENTS.get(random.nextInt(SAFARI_AGENTS.size()));
    }

    /**
     * Get a random Edge user agent
     */
    public static String edge() {
        return EDGE_AGENTS.get(random.nextInt(EDGE_AGENTS.size()));
    }

    /**
     * Get a random user agent from any browser
     */
    public static String random() {
        List<List<String>> allAgents = Arrays.asList(
                CHROME_AGENTS, FIREFOX_AGENTS, SAFARI_AGENTS, EDGE_AGENTS);
        List<String> selectedBrowser = allAgents.get(random.nextInt(allAgents.size()));
        return selectedBrowser.get(random.nextInt(selectedBrowser.size()));
    }
}
