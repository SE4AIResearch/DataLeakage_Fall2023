package com.github.cd721.data_leakage_plugin.data.leakage_data;

import com.github.cd721.data_leakage_plugin.data.Invocation;
import com.github.cd721.data_leakage_plugin.data.Utils;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;

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

}
