package com.github.cd721.data_leakage_plugin.enums;

import java.util.List;

/**
 * Corresponds to a source of overlap leakage
 */
public enum OverlapLeakageSourceKeyword implements LeakageSourceKeyword {
    sample("sample"),
    flow("flow");
    //TODO: add more

    private final String methodKeyword;

    OverlapLeakageSourceKeyword(String methodKeyword) {
        this.methodKeyword = methodKeyword;
    }


    @Override
    public String getTaintKeyword() {
        return switch (this) {
            case flow -> "TODO"; //TODO: add keyword
            case sample -> "split";
        };
    }


    @Override
    public List<LeakageCause> getPotentialCauses() {
        return switch (this) {
            case flow -> List.of(LeakageCause.DataAugmentation);
            case sample -> List.of(LeakageCause.SplitBeforeSample);
        };
    }

    @Override
    public String toString() {
        return methodKeyword;
    }


}
