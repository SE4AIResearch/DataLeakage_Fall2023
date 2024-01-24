package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.TaintLabel;

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

    public Taint findTaintThatMatchesText(String text) {
        return this.getLeakageSource().getTaints().stream().filter(
                taint ->taint.getPyCallExpression().equalsIgnoreCase(text) //equalsIgnoreCase MUST be used here
        ).findFirst().orElse(new Taint("", TaintLabel.rowset));//TODO: better error handling
    }

    @Override
    public boolean equals(Object obj){
        return this.lineNumber() == ((PreprocessingLeakageInstance)obj).lineNumber()
                &&this.invocation.getNumber() == ((PreprocessingLeakageInstance)obj).invocation().getNumber();
    }
}
