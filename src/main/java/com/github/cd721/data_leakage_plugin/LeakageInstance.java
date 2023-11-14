package com.github.cd721.data_leakage_plugin;

public class LeakageInstance {
    public int lineNumber;
    public LeakageType type;

    public LeakageInstance(int lineNumber, LeakageType type) {
        this.lineNumber = lineNumber;
        this.type = type;
    }
}
