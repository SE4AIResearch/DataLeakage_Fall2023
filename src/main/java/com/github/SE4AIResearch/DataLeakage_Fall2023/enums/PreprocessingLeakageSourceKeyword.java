package com.github.SE4AIResearch.DataLeakage_Fall2023.enums;

import java.util.List;

public enum PreprocessingLeakageSourceKeyword implements LeakageSourceKeyword {

    vector("vector"),

    select("select");

    //TODO: add more
    private final String methodKeyword;

    PreprocessingLeakageSourceKeyword(String methodKeyword) {
        this.methodKeyword = methodKeyword;
    }

    @Override
    public String getTaintKeyword() {
        return switch (this) {
            case vector -> "vector";
            case select -> "select";
        };
    }

    @Override
    public List<LeakageCause> getPotentialCauses() {
        return switch (this) {
            case vector -> List.of(LeakageCause.VectorizingTextData);
            case select -> List.of(LeakageCause.UsingTestDataForFeatureSelection);
        };
    }


    @Override
    public String toString() {
        return methodKeyword;
    }


}
