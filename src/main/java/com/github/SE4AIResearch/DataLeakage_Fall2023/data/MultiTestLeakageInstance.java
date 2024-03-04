package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.util.Objects;

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
    public String variableName() {
        return test;
    }

    @Override
    public LeakageSource getLeakageSource() {
       return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        MultiTestLeakageInstance otherInstance = (MultiTestLeakageInstance) obj;
        return this.lineNumber() == otherInstance.lineNumber()
              && this.invocation().getNumber() == otherInstance.invocation().getNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lineNumber(), this.invocation());
    }
}
