package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;

public interface LeakageInstance {
    int lineNumber();

    LeakageType type();

    Invocation invocation();

    LeakageSource getLeakageSource();

}
