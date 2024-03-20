package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.telemetry.OverlapLeakageTelemetry;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.util.Objects;

public class OverlapLeakageInstance implements LeakageInstance {

    private final int lineNumber;
    private final LeakageType type;

    private final Invocation invocation;
    private final LeakageSource leakageSource;
    private final String test;
    private final String train;

    public OverlapLeakageInstance(int lineNumber, Invocation invocation,
                                  String test, String train) {

        this.lineNumber = lineNumber;
        this.type = LeakageType.OverlapLeakage;
        this.invocation = invocation;

        this.leakageSource = new LeakageSource(this.type);
        this.test = Utils.stripSuffixFromVariableName(test);
        this.train = Utils.stripSuffixFromVariableName(train);
        ;
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
    public String variableName() {
        return test;
    }

    public String train() {
        return train;// TODO: this can also be considered a "variableName"
    }

    @Override
    public LeakageType type() {
        return type;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        OverlapLeakageInstance otherInstance = (OverlapLeakageInstance) obj;
        return this.lineNumber() == otherInstance.lineNumber()
                && this.invocation().getNumber() == otherInstance.invocation().getNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lineNumber(), this.invocation());
    }
}
