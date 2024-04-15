package com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageSource;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

public interface LeakageInstance {
    int lineNumber();

    LeakageType type();

    Invocation invocation();

    String variableName();//TODO: rename

    LeakageSource getLeakageSource();
    LeakageCause getCause();

}
