package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.LeakageInspection;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources.SourceElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public abstract class SourceInspection<T extends LeakageInstance, U extends LeakageSourceKeyword> extends LeakageInspection<T> {

    public abstract SourceElementVisitor<T, U> sourceElementVisitor(List<T> leakageInstances, @NotNull ProblemsHolder holder);



    @Override
    public PyElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {

        var leakageInstances = getLeakageInstancesForType(leakageAnalysisParser.LeakageInstances());
        return sourceElementVisitor(leakageInstances, holder);
    }

}
