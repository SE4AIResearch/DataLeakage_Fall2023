package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.MultiTestLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.LeakageQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

 class MultiTestLeakageQuickFix extends LeakageQuickFix<MultiTestLeakageInstance> {
    @NotNull
    @Override
    public String getName() {
        return InspectionBundle.get("inspectionText.removeRedundantTestEvaluations.quickfix.text");
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {


    }
}