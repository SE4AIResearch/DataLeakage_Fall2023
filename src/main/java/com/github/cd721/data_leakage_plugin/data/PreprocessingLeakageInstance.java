package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;

public class PreprocessingLeakageInstance extends LeakageInstance{
    private final String test;
    public PreprocessingLeakageInstance(int lineNumber,  Invocation invocation) {
        super(lineNumber, LeakageType.PreprocessingLeakage, invocation);
        this.test = Utils.getTrainFromPreprocessingLeakTelemetryFile();
    }

    public String test() {
        return test;
    }

    @Override
    public boolean equals(Object obj) {
        var leak = (PreprocessingLeakageInstance)obj;
        return this.lineNumber()==(leak.lineNumber())
                && this.invocation().getNumber()==(leak.invocation().getNumber());
    }
}
