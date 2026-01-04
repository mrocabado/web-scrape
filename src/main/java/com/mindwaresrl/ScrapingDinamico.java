package com.mindwaresrl;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import java.nio.file.Paths;

public class ScrapingDinamico {
    public static void main(String[] args) {
        // Usamos el bloque try-with-resources para asegurar que se cierre todo al fallar
        try (Playwright pw = Playwright.create()) {
            // 1. Lanzamos con argumentos para evitar detección
            Browser browser = pw.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(100)); // Hace que cada acción tarde 100ms (más humano)

            // 2. Definimos un contexto con resolución de pantalla común
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .setViewportSize(1280, 720));

            Page page = context.newPage();

            // 3. Navegación con tiempo de espera extendido
            System.out.println("Navegando a Airbnb...");
            page.navigate("https://www.airbnb.com", new Page.NavigateOptions().setTimeout(60000));
            
            // Esperamos a que cargue el contenido básico
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            // 4. Intento de click usando un selector de TEXTO (más estable que los IDs de test)
            // En lugar de 'structured-search...', buscamos el texto que ve el usuario
            try {
                System.out.println("Buscando el botón de búsqueda...");
                // Esperamos máximo 10 segundos por el botón
                page.waitForSelector("text='En cualquier lugar'", new Page.WaitForSelectorOptions().setTimeout(10000));
                page.click("text='En cualquier lugar'");
                
                // 5. Rellenar el destino
                page.fill("input[placeholder='Donde']", "Cochabamba, Bolivia");
                
                System.out.println("¡Interacción lograda!");
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("exito_airbnb.png")));
                
            } catch (Exception e) {
                System.err.println("No se encontró el botón. Posible bloqueo o cambio de diseño.");
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("error_captura.png")));
            }

            browser.close();
        }
    }
}