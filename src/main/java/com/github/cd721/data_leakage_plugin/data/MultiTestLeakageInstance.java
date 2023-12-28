package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;

public class MultiTestLeakageInstance extends LeakageInstance {

    private final String test;

    public MultiTestLeakageInstance(int lineNumber,  Invocation invocation) {
        super(lineNumber, LeakageType.MultiTestLeakage, invocation);
        this.test = Utils.getTestFromMultiUseTestLeakTelemetryFile();
    }

    public String test() {
        return test;
    }

//    @Override
//    public boolean equals(Object obj) {
//        var leak = (MultiTestLeakageInstance)obj;
//        return this.lineNumber()==(leak.lineNumber())
//                && this.invocation().getNumber()==(leak.invocation().getNumber());    }
}
