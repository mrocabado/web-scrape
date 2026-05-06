// com/mindwaresrl/common/Photo_Facebook.java
package com.mindwaresrl.common;

import com.microsoft.playwright.Page;
import com.mindwaresrl.common.facebook.*;
import com.mindwaresrl.model.facebook.PhotoData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Photo_Facebook {

    private final ResponseInterceptor interceptor   = new ResponseInterceptor();
    private final ReactionExtractor   reactions     = new ReactionExtractor();
    private final CommentExtractor    comments      = new CommentExtractor();
    private final AjaxDataParser      ajaxParser    = new AjaxDataParser();
    private final PhotoData           photoData     = new PhotoData();

    public Photo_Facebook(Page page, String url) {
        log.info("--- Iniciando scraping de foto ---");
        System.out.println("INICIADNO");
        prepare(page, url);
        collect(page);
        summarize();
    }

    private void prepare(Page page, String url) {
        page.onResponse(interceptor::handle);
        page.navigate(url);
        page.waitForTimeout(5000);
        trigger_face.removeDialogs(page);
        OverlayCleaner.purge(page);                  // <-- añadir
        page.waitForTimeout(1000);
    }

    private void collect(Page page) {
        photoData.getReactions().addAll(reactions.extract(page));
        photoData.getComments().addAll(comments.extract(page));
        photoData.getRawAjaxBodies().addAll(interceptor.getCapturedBodies());
        ajaxParser.parseAll(interceptor.getCapturedBodies(), photoData);
    }

    private void summarize() {
        System.out.println("\n╔══════════════ RESUMEN FINAL ══════════════╗");
        System.out.println("║ Reacciones POST: " + photoData.getReactions().size());
        System.out.println("║ Comentarios:     " + photoData.getComments().size());
        System.out.println("║ AJAX bodies:     " + photoData.getRawAjaxBodies().size());
        System.out.println("╚════════════════════════════════════════════╝");
    }
}