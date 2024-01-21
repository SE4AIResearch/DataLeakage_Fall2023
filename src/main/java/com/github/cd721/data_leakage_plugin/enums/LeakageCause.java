package com.github.cd721.data_leakage_plugin.enums;

public enum LeakageCause {
    SplitBeforeSample,
    DataAugmentation,
    VectorizingTextData, unknown;

    public String getInspectionTextKey() {
        return switch (this) {
            case DataAugmentation -> "inspectionText.dataAugmentationWarning.text";
            case SplitBeforeSample -> "inspectionText.splitBeforeSampleReminder.text";
            case VectorizingTextData -> "inspectionText.vectorizerFitWarning.text";
            case unknown->"";
        };
    }

}
