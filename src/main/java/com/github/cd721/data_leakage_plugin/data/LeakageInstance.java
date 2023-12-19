package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.inspections.LeakageInspection;

public class LeakageInstance {
    private final int lineNumber;
    private final LeakageType type;
    private final Invocation invocation;

    public LeakageInstance(int lineNumber, LeakageType type, Invocation invocation) {
        this.lineNumber = lineNumber
        ;
        this.type = type;
        this.invocation = invocation;
    }

    public int lineNumber() {
        return this.lineNumber;
    }

    public LeakageType type() {
        return this.type;
    }

    public Invocation invocation() {
        return this.invocation;
    }

    public static Class<? extends LeakageInstance> getInstanceTypeForLeakageType(LeakageType leakageType) {
        switch (leakageType) {
            case OverlapLeakage -> {
                return OverlapLeakageInstance.class;
            }
            case MultiTestLeakage -> {
                return MultiTestLeakageInstance.class;
            }
            case PreprocessingLeakage -> {
                return PreprocessingLeakageInstance.class;
            }
            default -> {
                return OverlapLeakageInstance.class;

            }
        }

    }


}
