package com.mindwaresrl.common;

import com.mindwaresrl.model.ProfileMode;

public class BrowserProfileSelector {

    public static BrowserProfileStrategy resolve(ProfileMode mode) {
        return switch (mode) {
            case DESKTOP -> new DesktopProfileStrategy();
            case MOBILE -> new MobileProfileStrategy();
            case AUTO -> new AutoProfileStrategy();
        };
    }
}
