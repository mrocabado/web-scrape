package com.mindwaresrl.common;

import com.mindwaresrl.model.PageSnapshot;

public interface BlockDetector {
    boolean isBLocked(PageSnapshot result);
}
