package com.mindwaresrl.common;

public final class user_agent_update {
    private user_agent_update(){}

    public static String chrome120Windows() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/120.0.6099.130 Safari/537.36";
    }

    public static String chrome121Windows() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/121.0.6167.85 Safari/537.36";
    }

    public static String firefox115Windows() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:115.0) " +
                "Gecko/20100101 Firefox/115.0";
    }


    public static String chrome120Mac() {
        return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/120.0.6099.130 Safari/537.36";
    }


    public static String chrome119Android() {
        return "Mozilla/5.0 (Linux; Android 12; Pixel 5) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/119.0.6045.193 Mobile Safari/537.36";
    }

    public static String chrome120Android() {
        return "Mozilla/5.0 (Linux; Android 13; Pixel 6) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/120.0.6099.144 Mobile Safari/537.36";
    }
}
