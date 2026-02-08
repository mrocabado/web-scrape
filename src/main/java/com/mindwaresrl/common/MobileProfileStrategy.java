package com.mindwaresrl.common;

import com.mindwaresrl.model.BrowserProfile;

public class MobileProfileStrategy implements BrowserProfileStrategy {

    @Override
    public BrowserProfile build() {
        return new BrowserProfile(
                UserAgentUpdate.randomMobile(),
                375,
                812,
                "es-ES",
                "America/La_Paz"
        );
    }
}
