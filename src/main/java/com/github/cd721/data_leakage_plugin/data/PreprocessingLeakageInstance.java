package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;

public class PreprocessingLeakageInstance implements LeakageInstance{
    private final String test;
    private final int lineNumber;
    private final LeakageType type;

    private final Invocation invocation;
    public PreprocessingLeakageInstance(int lineNumber,  Invocation invocation) {
        this.lineNumber = lineNumber;
        this.type = LeakageType.PreprocessingLeakage;
        this.invocation = invocation;
        this.test = Utils.getTrainFromPreprocessingLeakTelemetryFile();
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
}
