package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public abstract class LeakageQuickFix<T> implements LocalQuickFix {
    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return getName();
    }

    @Override
    public abstract void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor);

}
