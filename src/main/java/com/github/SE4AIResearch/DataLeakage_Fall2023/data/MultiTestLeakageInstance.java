package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

public class MultiTestLeakageInstance implements LeakageInstance {

    private final String test;
    private final int lineNumber;
    private final LeakageType type;

    private final Invocation invocation;

    public MultiTestLeakageInstance(int lineNumber, Invocation invocation) {
        this.lineNumber = lineNumber;
        this.type = LeakageType.MultiTestLeakage;
        this.invocation = invocation;
        this.test = Utils.getTestFromMultiUseTestLeakTelemetryFile();
    }

    public String test() {
        return test;
    }

    @Override
    public int lineNumber() {
        return lineNumber;
    }

    @Override
    public LeakageType type() {
        return type;
    }

    @Override
    public Invocation invocation() {
        return invocation;
    }

    @Override
    public LeakageSource getLeakageSource() {
        return null;
    }

    @Override
    public boolean equals(Object obj){
        return this.lineNumber() == ((MultiTestLeakageInstance)obj).lineNumber()
                &&this.invocation.getNumber() == ((MultiTestLeakageInstance)obj).invocation().getNumber();
    }
}
