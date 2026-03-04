package com.mindwaresrl.common;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.mindwaresrl.common.Photo_Facebook;
import com.mindwaresrl.common.Post_facebook;
public class trigger_face {
    public trigger_face(Page page, String Url){
        page.navigate(Url);
        try{
            page.waitForTimeout(3000);
            Locator dialog = page.locator("div[role='dialog']");
            if (dialog.count() > 0) {
                page.evaluate(
                        "document.querySelectorAll(\"div[role='dialog']\").forEach(e => e.remove());"
                );
                page.evaluate("document.body.style.overflow='auto'");
            }
            redirection(Url,page);
        }catch(Exception e){

        }
        //page.waitForTimeout(5000);
    }

    private void redirection(String url,Page page){
        if(url.contains("/posts/")) {
            new Post_facebook(page);
        }
        if(url.contains("photo/")){
            new Photo_Facebook(page);
        }
    }
}
