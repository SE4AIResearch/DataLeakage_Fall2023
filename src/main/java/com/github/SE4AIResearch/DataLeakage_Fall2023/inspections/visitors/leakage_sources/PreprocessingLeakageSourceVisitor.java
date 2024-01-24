package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.PreprocessingLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class PreprocessingLeakageSourceVisitor extends SourceElementVisitor<PreprocessingLeakageInstance, PreprocessingLeakageSourceKeyword> {
    private final List<PreprocessingLeakageInstance> preprocessingLeakageInstances;
    public PsiRecursiveElementVisitor recursiveElementVisitor;

    public PreprocessingLeakageSourceVisitor(List<PreprocessingLeakageInstance> preprocessingLeakageInstances, @NotNull ProblemsHolder holder) {
        this.preprocessingLeakageInstances = preprocessingLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                // super.visitElement(element);//TODO: do we need this?
                renderInspectionOnLeakageSource(element,holder, preprocessingLeakageInstances);
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


    //TODO: consider different making different visitors for performance
    @Override
    public void visitPyCallExpression(@NotNull PyCallExpression node) {//TODO: consider moving this into visitPyReferenceExpression.. will require some refactoring.

        //TODO: extract

        if (!preprocessingLeakageInstances.isEmpty()) {
            if (leakageSourceIsAssociatedWithNode(preprocessingLeakageInstances, node)) {

                renderInspectionOnLeakageSource(node, holder, preprocessingLeakageInstances);
            }


            var keywords = Arrays.stream(PreprocessingLeakageSourceKeyword.values()).toList();
            for (PreprocessingLeakageSourceKeyword keyword : keywords) {
                preprocessingLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().ifPresent(instance -> renderInspectionOnTaintWithCause(node, holder, instance.getLeakageSource().getCause(), keyword));


            }
        }
    }


    @Override
    public void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<PreprocessingLeakageInstance> leakageInstances) {

        //TODO: change name?
        preprocessingLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().ifPresent(
                instance -> holder.registerProblem(node, getInspectionMessageForLeakageSource(instance, node))
        );


        //TODO: rendering multiple messages on some leakage sources sometimes

    }

    @NotNull
    private static String getInspectionMessageForLeakageSource(PreprocessingLeakageInstance leakageInstance, @NotNull PsiElement node) {
        StringBuilder inspectionMessage = new StringBuilder(InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey()));
        inspectionMessage.append(" ");

        var cause = leakageInstance.getLeakageSource().getCause();
        inspectionMessage.append(InspectionBundle.get(cause.getInspectionTextKey()));

        return inspectionMessage.toString();
    }


}