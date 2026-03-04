package com.mindwaresrl.common;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public class Photo_Facebook {
    public Photo_Facebook(Page page){

        System.out.println("-------------------------------------entro a fotos--------------------------------");
        page.onResponse(response -> {
            //System.out.println(response.url());
        });

        Locator reactions = page.locator("div[aria-label]");
        int count = reactions.count();

        for (int i = 0; i < count; i++) {
            String label = reactions.nth(i).getAttribute("aria-label");

            if (label != null && label.matches(".*\\d+.*")) {
                System.out.println(label);
            }
        }

        page.onResponse(response -> {
            String url = response.url();

            if (url.contains("ajax/bz") && response.status() == 200) {
                try {
                    String body = response.text();

                    if (body.contains("feedback") || body.contains("comment")) {
                        System.out.println("------ POSIBLE DATA ------");
                        System.out.println(body);
                    }

                } catch (Exception ignored) {}
            }
        });
    }
}
