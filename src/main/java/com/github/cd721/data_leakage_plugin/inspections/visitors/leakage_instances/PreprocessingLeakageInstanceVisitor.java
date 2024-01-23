package com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances;

import com.github.cd721.data_leakage_plugin.data.PreprocessingLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.PreprocessingLeakageSourceKeyword;
import com.github.cd721.data_leakage_plugin.inspections.InspectionBundle;
import com.github.cd721.data_leakage_plugin.inspections.PsiUtils;
import com.github.cd721.data_leakage_plugin.inspections.visitors.ElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class PreprocessingLeakageInstanceVisitor extends InstanceElementVisitor<PreprocessingLeakageInstance> {
    private final List<PreprocessingLeakageInstance> preprocessingLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;

    public PreprocessingLeakageInstanceVisitor(List<PreprocessingLeakageInstance> preprocessingLeakageInstances, @NotNull ProblemsHolder holder) {
        this.preprocessingLeakageInstances = preprocessingLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageInstance(preprocessingLeakageInstances,element);
            }
        };
    }

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.PreprocessingLeakage;
    }

    @Override
    public Predicate<PreprocessingLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);
        return instance -> (instance.lineNumber() == nodeLineNumber)
                && Objects.equals(instance.test(), node.getText()); //TODO: make sure it's ok to have text and not name
    }

    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
        renderInspectionOnLeakageInstance(preprocessingLeakageInstances, node);

    }



    @Override
    public void visitPyNamedParameter(@NotNull PyNamedParameter node) {

        if (leakageIsAssociatedWithNode(preprocessingLeakageInstances, node)) {
            holder.registerProblem(node, InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey()));

        }
    }

    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }

}
