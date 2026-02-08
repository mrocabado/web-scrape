package com.mindwaresrl.common;

import com.mindwaresrl.model.BrowserProfile;

public class AutoProfileStrategy implements BrowserProfileStrategy {

    private final DesktopProfileStrategy desktop = new DesktopProfileStrategy();

    @Override
    public BrowserProfile build() {
        return desktop.build();
    }
}
