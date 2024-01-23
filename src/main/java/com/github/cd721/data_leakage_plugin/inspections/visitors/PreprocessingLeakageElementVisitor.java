package com.github.cd721.data_leakage_plugin.inspections.visitors;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.data.PreprocessingLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.OverlapLeakageSourceKeyword;
import com.github.cd721.data_leakage_plugin.enums.PreprocessingLeakageSourceKeyword;
import com.github.cd721.data_leakage_plugin.inspections.InspectionBundle;
import com.github.cd721.data_leakage_plugin.inspections.PsiUtils;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.InstanceElementVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.OverlapLeakageInstanceVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.PreprocessingLeakageInstanceVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources.OverlapLeakageSourceVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources.PreprocessingLeakageSourceVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources.SourceElementVisitor;
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

public class PreprocessingLeakageElementVisitor extends ElementVisitor<PreprocessingLeakageInstance, PreprocessingLeakageSourceKeyword> {
    private final List<PreprocessingLeakageInstance> preprocessingLeakageInstances;
    public PsiRecursiveElementVisitor recursiveElementVisitor;

    private final InstanceElementVisitor<PreprocessingLeakageInstance> preprocessingLeakageInstanceVisitor;
    private final SourceElementVisitor<PreprocessingLeakageInstance, PreprocessingLeakageSourceKeyword> preprocessingLeakageSourceVisitor;

    public PreprocessingLeakageElementVisitor(List<PreprocessingLeakageInstance> preprocessingLeakageInstances, @NotNull ProblemsHolder holder) {
        this.preprocessingLeakageInstances = preprocessingLeakageInstances;
        this.holder = holder;
        this.preprocessingLeakageInstanceVisitor = new PreprocessingLeakageInstanceVisitor(preprocessingLeakageInstances, holder);
        this.preprocessingLeakageSourceVisitor = new PreprocessingLeakageSourceVisitor(preprocessingLeakageInstances, holder);

        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                // super.visitElement(element);//TODO: do we need this?
                preprocessingLeakageInstanceVisitor.renderInspectionOnLeakageInstance(preprocessingLeakageInstances, element);
                preprocessingLeakageSourceVisitor.renderInspectionOnLeakageSource(element, holder, preprocessingLeakageInstances);
            }
        };
    }

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.PreprocessingLeakage;
    }


    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }


}
