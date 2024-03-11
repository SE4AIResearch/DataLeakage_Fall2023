package com.github.SE4AIResearch.DataLeakage_Fall2023.enums;

public enum LeakageCause {
    SplitBeforeSample,
    DataAugmentation,
    VectorizingTextData,

    RepeatDataEvaluation,
    unknown;

    public String getInspectionTextKey() {
        return switch (this) {
            case DataAugmentation -> "inspectionText.dataAugmentationWarning.text";
            case SplitBeforeSample -> "inspectionText.splitBeforeSampleReminder.text";
            case VectorizingTextData -> "inspectionText.vectorizerFitWarning.text";
            case RepeatDataEvaluation -> "inspectionText.repeatDataEvaluationWarning.text";
            case unknown -> "inspectionText.generalWarning.text";
        };
    }

}
