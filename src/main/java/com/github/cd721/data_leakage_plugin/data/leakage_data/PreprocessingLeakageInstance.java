package com.github.cd721.data_leakage_plugin.data.leakage_data;

import com.github.cd721.data_leakage_plugin.data.Invocation;
import com.github.cd721.data_leakage_plugin.data.Utils;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;

import java.util.List;

public class PreprocessingLeakageInstance implements LeakageInstance {
    private final String test;
    private final int lineNumber;
    private final LeakageType type;
    private final Invocation invocation;
    private final LeakageSource leakageSource;

    public PreprocessingLeakageInstance(int lineNumber, Invocation invocation) {
        this.lineNumber = lineNumber;
        this.type = LeakageType.PreprocessingLeakage;
        this.invocation = invocation;
        this.test = Utils.getTrainFromPreprocessingLeakTelemetryFile();
        this.leakageSource = new LeakageSource(this.type);
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

    public LeakageSource getLeakageSource() {
        return leakageSource;
    }
}
