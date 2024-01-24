package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public abstract class InstanceElementVisitor<T extends LeakageInstance> extends PyElementVisitor {
    public ProblemsHolder holder;

    public abstract LeakageType getLeakageType();

    public abstract Predicate<T> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node);


    public boolean leakageIsAssociatedWithNode(List<T> leakageInstances, @NotNull PsiElement node) {
        return leakageInstances.stream().anyMatch(leakageInstanceIsAssociatedWithNode(node));
    }





    public void renderInspectionOnLeakageInstance(List<T> leakageInstances, PsiElement node ){
        if (leakageIsAssociatedWithNode(leakageInstances, node)) {
            LeakageType leakageType = getLeakageType();
            holder.registerProblem(node, InspectionBundle.get(leakageType.getInspectionTextKey()));

        }
    }



}
