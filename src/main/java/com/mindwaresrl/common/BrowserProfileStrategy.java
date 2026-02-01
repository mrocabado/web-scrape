package com.mindwaresrl.common;

import com.mindwaresrl.model.BrowserProfile;
import com.mindwaresrl.model.ProfileContext;


public interface BrowserProfileStrategy {
    BrowserProfile getProfile(ProfileContext context);
}
