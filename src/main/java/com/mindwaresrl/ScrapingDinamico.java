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
            try {
                System.out.println("Navegando a Airbnb...");
                page.navigate("https://www.airbnb.com", new Page.NavigateOptions().setTimeout(60000));
                
                // Esperamos a que cargue el contenido básico
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);

                // --- NUEVA ESTRATEGIA: CERRAR MODALES PRIMERO ---
                try {
                    // Intenta cerrar el modal de cookies/info si aparece "Entendido"
                     if (page.isVisible("button:has-text('Entendido')")) {
                         page.click("button:has-text('Entendido')");
                         System.out.println("Modal cerrado.");
                     }
                } catch (Exception e) {
                    
                }
                String inputSelector = "input[data-testid='structured-search-input-field-query']";
                
                // Si no encontramos ese ID específico, intentamos con el ID alternativo que suele usar Airbnb
                if (!page.isVisible(inputSelector)) {
                     inputSelector = "#bigsearch-query-location-input";
                }

                // A veces hay que hacer click en el botón de "En cualquier lugar" si el search está colapsado
                if (!page.isVisible(inputSelector)) {
                    System.out.println("Buscando botón para expandir búsqueda...");
                    page.click("button:has-text('En cualquier lugar')");
                }

                System.out.println("Escribiendo destino en selector: " + inputSelector);
                page.waitForSelector(inputSelector);
                page.click(inputSelector); // Click para enfocar
                page.fill(inputSelector, "Cochabamba, Bolivia");
                
                // Presionar ENTER para buscar (más fiable que buscar el botón de lupa)
                page.keyboard().press("Enter");
                
                System.out.println("¡Búsqueda enviada!");
                page.waitForTimeout(3000); // Esperar un poco para la foto
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("exito_airbnb.png")));
                
            } catch (Exception e) {
                System.err.println("Error durante la automatización: " + e.getMessage());
                e.printStackTrace();
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("error_captura.png")));
            }

            browser.close();
        }
    }
}