package com.github.cd721.data_leakage_plugin.data.leakage_data;

import com.github.cd721.data_leakage_plugin.data.Invocation;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;

public interface LeakageInstance {
    int lineNumber();

    LeakageType type();

    Invocation invocation();


}
