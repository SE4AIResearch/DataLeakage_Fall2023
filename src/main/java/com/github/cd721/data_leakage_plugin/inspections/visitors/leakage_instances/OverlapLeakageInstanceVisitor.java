package com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.data.taints.Taint;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.OverlapLeakageSourceKeyword;
import com.github.cd721.data_leakage_plugin.inspections.InspectionBundle;
import com.github.cd721.data_leakage_plugin.inspections.PsiUtils;
import com.github.cd721.data_leakage_plugin.inspections.visitors.ElementVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources.OverlapLeakageSourceVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources.SourceElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A type of {@link PyElementVisitor} that visits different types of elements within the PSI tree,
 * such as {@link PyReferenceExpression}s.
 */
public class OverlapLeakageInstanceVisitor extends InstanceElementVisitor<OverlapLeakageInstance> {
    private final List<OverlapLeakageInstance> overlapLeakageInstances;
private final PsiRecursiveElementVisitor recursiveElementVisitor;

    public OverlapLeakageInstanceVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = overlapLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageInstance(overlapLeakageInstances,element);
            }
        };
    }


    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }

    @Override
    public Predicate<OverlapLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

        return instance -> (instance.lineNumber() == nodeLineNumber) && Objects.equals(instance.test(), node.getText()); //TODO: make sure it's ok to have text and not name
    }

    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {

        renderInspectionOnLeakageInstance(overlapLeakageInstances, node);
    }
    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }

}
