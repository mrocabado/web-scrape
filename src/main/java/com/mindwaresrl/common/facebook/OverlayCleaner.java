package com.mindwaresrl.common.facebook;

import com.microsoft.playwright.Page;

public class OverlayCleaner {


    public static void purge(Page page) {
        try {
            Object removed = page.evaluate("""
                () => {
                  let n = 0;
                  // Diálogos modales
                  document.querySelectorAll('[role="dialog"]').forEach(el => { el.remove(); n++; });
                  // Cualquier overlay fixed con z-index alto que cubra la pantalla
                  document.querySelectorAll('div').forEach(el => {
                    const cs = window.getComputedStyle(el);
                    if (cs.position === 'fixed'
                        && parseInt(cs.zIndex || '0', 10) > 100
                        && el.offsetWidth >= window.innerWidth * 0.8
                        && el.offsetHeight >= window.innerHeight * 0.8) {
                      el.remove(); n++;
                    }
                  });
                  // Form de login flotante
                  document.querySelectorAll('form').forEach(f => {
                    if (f.querySelector('input[name="email"]')
                        || f.querySelector('input[name="pass"]')) {
                      const host = f.closest('div[class]');
                      if (host) { host.remove(); n++; }
                    }
                  });
                  // Desbloquear scroll si FB lo bloqueó
                  document.documentElement.style.overflow = 'auto';
                  document.body.style.overflow = 'auto';
                  return n;
                }
            """);
            System.out.println("[OVERLAY] Elementos removidos: " + removed);
        } catch (Exception e) {
            System.out.println("[OVERLAY] Error purgando: " + e.getMessage());
        }
    }
}