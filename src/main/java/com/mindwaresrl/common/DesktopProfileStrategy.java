package com.mindwaresrl.common;

import com.mindwaresrl.model.BrowserProfile;

public class DesktopProfileStrategy implements BrowserProfileStrategy{
    @Override
    public BrowserProfile   build(){
        return new BrowserProfile(
                UserAgentUpdate.randomDesktop(),
                1920,
                1080,
                "es-Es",
                "America/La_Paz"
        );
    }
}

