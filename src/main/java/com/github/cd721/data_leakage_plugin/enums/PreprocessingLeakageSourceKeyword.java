package com.github.cd721.data_leakage_plugin.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PreprocessingLeakageSourceKeyword implements LeakageSourceKeyword {

    predict("predict");
    //TODO: add more
    private final String methodKeyword;

    PreprocessingLeakageSourceKeyword(String methodKeyword) {
        this.methodKeyword = methodKeyword;
    }

    @Override
    public String getTaintKeyword() {
        return switch (this) {
            case predict -> "transform";
        };
    }

    @Override
    public List<LeakageCause> getPotentialCauses() {
        return switch (this) {

            case predict -> List.of(LeakageCause.VectorizingTextData);

        };
    }


    @Override
    public String toString() {
        return methodKeyword;
    }


}
