package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.util.Objects;

public interface LeakageInstance {
    int lineNumber();

    LeakageType type();

    Invocation invocation();

    String variableName();//TODO: rename

    LeakageSource getLeakageSource();
}
