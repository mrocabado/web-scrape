package com.mindwaresrl.common;

import com.mindwaresrl.model.BrowserProfile;
import com.mindwaresrl.model.PorfileContext;


public interface BrowserProfileStrategy {
    BrowserProfile getprofile(PorfileContext context);
}
