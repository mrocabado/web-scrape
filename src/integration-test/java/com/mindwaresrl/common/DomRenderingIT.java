package com.mindwaresrl.common;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class DomRenderingIT {

    @Test
    void givenSimpleScrape_whenDomRenderingExecutes_thenResultIsFalse() throws MalformedURLException {
        // GIVEN
        URL staticWebSite = URI.create("https://www.lostiempos.com/").toURL();

        // WHEN
        var usesDynamicRendering = DomRendering.usesDynamicRendering(staticWebSite);

        // THEN
        assertThat(usesDynamicRendering).isFalse();
    }

    @Test
    void givenDynamicSite_whenDomRenderingExecutes_thenResultIsTrue() throws MalformedURLException {
        // GIVEN
        var staticWebSite = URI.create("https://www.facebook.com/CBAFanPage/posts/felicidades-a-los-lectores-cbabiblioteca/1906082259562953/").toURL();

        // WHEN
        var usesDynamicRendering = DomRendering.usesDynamicRendering(staticWebSite);

        // THEN
        assertThat(usesDynamicRendering).isTrue();
    }
}
