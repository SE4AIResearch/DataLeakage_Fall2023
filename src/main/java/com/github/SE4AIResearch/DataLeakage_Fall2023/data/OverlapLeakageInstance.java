package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

public class OverlapLeakageInstance implements LeakageInstance {
    private final String test;
    private final int lineNumber;
    private final LeakageType type;

    private final Invocation invocation;
    private final LeakageSource leakageSource;

    public OverlapLeakageInstance(int lineNumber, Invocation invocation) {
        this.lineNumber = lineNumber;
        this.type = LeakageType.OverlapLeakage;
        this.invocation = invocation;
        this.test = Utils.getTestFromOverlapLeakTelemetryFile();
        this.leakageSource = new LeakageSource(this.type);
    }

    public String test() {
        return test;
    }

    public LeakageSource getLeakageSource() {
        return leakageSource;
    }


    @Override
    public int lineNumber() {
        return lineNumber;
    }

    @Override
    public Invocation invocation() {
        return invocation;
    }

    @Override
    public LeakageType type() {
        return type;
    }

    @Override
    public boolean equals(Object obj){
        return this.lineNumber() == ((OverlapLeakageInstance)obj).lineNumber()
                &&this.invocation.getNumber() == ((OverlapLeakageInstance)obj).invocation().getNumber();
    }

}
