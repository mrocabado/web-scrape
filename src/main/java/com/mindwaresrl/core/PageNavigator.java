
package com.mindwaresrl.core;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

public class PageNavigator {

    private final Page page;

    public PageNavigator(Page page){
        this.page=page;
    }
    public Response gotoURl(String url, String strategy){
       // WaitUntilState waitState = "DYNAMIC".equalsIgnoreCase(strategy)
         //       ? WaitUntilState.NETWORKIDLE
           //     :WaitUntilState.DOMCONTENTLOADED;

        try {

            Response response = page.navigate(url,
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                    //.setTimeout(10000));


            if ("DYNAMIC".equalsIgnoreCase(strategy)) {
                try {

                    page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(5000));

                } catch (PlaywrightException e) {
                    System.out.println("⚠️ La red sigue activa tras 5s (ignorando timeout). Procediendo a extraer...");
                }
                simulateHumanScroll();
            }

            return response;

        } catch (TimeoutError e) {
            System.err.println("⚠️ Timeout cargando la página principal. Intentando recuperar HTML parcial...");
            return null;
        }
    }
    public void simulateHumanScroll (){

        try{
            page.evaluate("window.scrollBy(0, 500)");
            Thread.sleep(500);
        }catch (Exception e){}
    }

}
