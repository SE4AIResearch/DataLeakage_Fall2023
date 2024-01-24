package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

public interface LeakageInstance {
    int lineNumber();

    LeakageType type();

    Invocation invocation();

    LeakageSource getLeakageSource();


}
