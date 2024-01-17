package com.github.cd721.data_leakage_plugin.enums;
/**
 * Corresponds to a source of overlap leakage
 */
public enum OverlapLeakageSourceKeyword {
    sample("sample"),
    flow("flow");

    private final String methodKeyword;

    OverlapLeakageSourceKeyword(String methodKeyword) {
        this.methodKeyword = methodKeyword;
    }


    public String getTaintKeyword() {
        return switch (this) {
            case flow -> "TODO";
            case sample -> "split";
        };
    }


    public LeakageCause getCause() {
        return switch (this) {
            case flow -> LeakageCause.DataAugmentation;
            case sample -> LeakageCause.SplitBeforeSample;
        };
    }

    @Override
    public String toString() {
        return methodKeyword;
    }
}
