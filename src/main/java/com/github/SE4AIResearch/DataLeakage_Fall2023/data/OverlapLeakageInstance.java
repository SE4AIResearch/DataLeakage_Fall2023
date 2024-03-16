package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.telemetry.OverlapLeakageTelemetry;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.util.Objects;

public class OverlapLeakageInstance implements LeakageInstance {

    private final int lineNumber;
    private final LeakageType type;

    private final Invocation invocation;
    private final LeakageSource leakageSource;
    private final OverlapLeakageTelemetry telemetry = new OverlapLeakageTelemetry();
    private final String test = telemetry.getTest();
    private final String train = telemetry.getTrain();
    ;

    public OverlapLeakageInstance(int lineNumber, Invocation invocation) {

        this.lineNumber = lineNumber;
        this.type = LeakageType.OverlapLeakage;
        this.invocation = invocation;

        this.leakageSource = new LeakageSource(this.type);

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
