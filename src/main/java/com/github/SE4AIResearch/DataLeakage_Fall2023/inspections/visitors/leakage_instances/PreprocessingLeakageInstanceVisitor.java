package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.warning_renderers.DataLeakageWarningRenderer;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class PreprocessingLeakageInstanceVisitor extends InstanceElementVisitor<PreprocessingLeakageInstance> {
    private final List<PreprocessingLeakageInstance> preprocessingLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;

    Collection<RangeHighlighter> collection = new ArrayList<>();

    public PreprocessingLeakageInstanceVisitor(List<PreprocessingLeakageInstance> preprocessingLeakageInstances, @NotNull ProblemsHolder holder) {
        this.preprocessingLeakageInstances = preprocessingLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageInstance(preprocessingLeakageInstances, element);
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
        return instance -> (instance.lineNumber() == nodeLineNumber) && (instance.variableName().contains(node.getText())); //TODO: make sure it's ok to have text and not name
    }


    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
//      var log=  Logger.getInstance(PreprocessingLeakageInstanceVisitor.class);
//        log.warn("HERE***********************************************************");
        renderInspectionOnLeakageInstance(preprocessingLeakageInstances, node);

    }


    @Override
    public void visitPyNamedParameter(@NotNull PyNamedParameter node) {

        if (leakageIsAssociatedWithNode(preprocessingLeakageInstances, node)) {
            var inspectionMessage = InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey());
            DataLeakageWarningRenderer.renderDataLeakageWarning(node, holder, inspectionMessage, collection);

        }

    }

    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }




}
