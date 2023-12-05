package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;

public record LeakageInstance(int lineNumber, LeakageType type, Invocation invocation) {
    public int lineNumber() {
        return this.lineNumber;
    }

    public LeakageType type() {
        return this.type;
    }

    public Invocation invocation(){
        return this.invocation;
    }
    
}
