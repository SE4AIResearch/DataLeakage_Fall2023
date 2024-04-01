package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;


import com.github.SE4AIResearch.DataLeakage_Fall2023.data.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.LeakageQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class PreprocessingLeakageQuickFix extends LeakageQuickFix<PreprocessingLeakageInstance> {
    private final List preprocessingLeakageInstances;

    protected PreprocessingLeakageQuickFix(List<PreprocessingLeakageInstance> instances) {
        this.preprocessingLeakageInstances = instances;
    }

    @NotNull
    @Override
    public String getName() {
        return InspectionBundle.get("inspectionText.vectorizingTextData.quickfix.text");
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {





    }
}
