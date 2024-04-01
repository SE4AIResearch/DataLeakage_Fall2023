package com.github.SE4AIResearch.DataLeakage_Fall2023.enums;

import java.util.List;

public class LeakageSourceKeywordFactory {
    public static LeakageSourceKeyword[] getSourceKeywordValuesForleakageType(LeakageType type) {
        return switch (type) {
            case OverlapLeakage -> OverlapLeakageSourceKeyword.values();
            case PreprocessingLeakage -> PreprocessingLeakageSourceKeyword.values();
            case MultiTestLeakage -> new LeakageSourceKeyword[0];//TODO: no need fo this case
        };
    }
}
