package com.github.cd721.data_leakage_plugin.enums;

public enum LeakageType {
    OverlapLeakage,
    PreprocessingLeakage,
    MultiTestLeakage;

    public String getInspectionTextKey() {
        return switch (this) {
            case OverlapLeakage -> "inspectionText.overlapLeakage.text";
            case PreprocessingLeakage -> "inspectionText.preprocessingLeakage.text";
            case MultiTestLeakage -> "inspectionText.multiTestLeakage.text";

        };
    }
}
