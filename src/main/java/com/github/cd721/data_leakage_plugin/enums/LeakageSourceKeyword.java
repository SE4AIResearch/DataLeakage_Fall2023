package com.github.cd721.data_leakage_plugin.enums;

public interface LeakageSourceKeyword {
    String getTaintKeyword();

    LeakageCause getCause();


    @Override
    String toString();
}
