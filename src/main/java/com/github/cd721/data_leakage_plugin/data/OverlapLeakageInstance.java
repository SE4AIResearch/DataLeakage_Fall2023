package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.data.taints.Taint;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.TaintLabel;

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

    public Taint findTaintThatMatchesText(String text) {
        return this.getLeakageSource().getTaints().stream().filter(
                taint ->taint.getPyCallExpression().equalsIgnoreCase(text) //equalsIgnoreCase MUST be used here
        ).findFirst().orElse(new Taint("", TaintLabel.dup));//TODO: better error handling
    }

    @Override
    public boolean equals(Object obj){
        return this.lineNumber() == ((OverlapLeakageInstance)obj).lineNumber()
                &&this.invocation.getNumber() == ((OverlapLeakageInstance)obj).invocation().getNumber();
    }

}
