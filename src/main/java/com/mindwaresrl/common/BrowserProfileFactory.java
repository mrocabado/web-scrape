package com.mindwaresrl.common;

import com.mindwaresrl.model.BrowserProfile;

import com.mindwaresrl.model.ProfileContext;
import com.mindwaresrl.common.user_agent_update;

class DesktopProfileStrategy implements BrowserProfileStrategy {
    @Override
    public BrowserProfile getProfile(ProfileContext ctx) {
        return new BrowserProfile(
                user_agent_update.chrome120Windows(),
                1920,
                1080,
                "es-ES",
                "America/La_Paz"
        );
    }
}
class MobileProfileStrategy implements BrowserProfileStrategy {

    @Override
    public BrowserProfile getProfile(ProfileContext ctx) {
        return new BrowserProfile(
                user_agent_update.chrome119Android(),
                375,
                812,
                "es-ES",
                "America/La_Paz"
        );
    }
}

class DesktopFallbackProfileStrategy implements BrowserProfileStrategy{
    @Override
    public BrowserProfile getProfile(ProfileContext ctx){
        return new BrowserProfile(
                user_agent_update.firefox115Windows(),
                1920,
                1080,
                "es-ES",
                "America/La_Paz"
        );
    }
}



public class BrowserProfileFactory {
    public static BrowserProfileStrategy getStrategy(ProfileContext ctx){
        return switch (ctx.mode()){
            case MOBILE ->  new MobileProfileStrategy();
            case DESKTOP -> new DesktopProfileStrategy();
            case AUTO -> new DesktopFallbackProfileStrategy() ;
        };
    }
}
