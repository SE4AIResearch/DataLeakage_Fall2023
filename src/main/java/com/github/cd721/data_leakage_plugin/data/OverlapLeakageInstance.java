package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;

public class OverlapLeakageInstance extends LeakageInstance{
    private final String test;
    public OverlapLeakageInstance(int lineNumber,  Invocation invocation) {
        super(lineNumber, LeakageType.OverlapLeakage, invocation);
        this.test = Utils.getTestFromOverlapLeakTelemetryFile();
    }

    public String test() {
        return test;
    }

    @Override
    public boolean equals(Object obj) {
        var leak = (OverlapLeakageInstance)obj;
        return this.lineNumber()==(leak.lineNumber())
                && this.invocation().getNumber()==(leak.invocation().getNumber());    }

}
