package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.PreprocessingLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.PreprocessingLeakageSourceKeyword;
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

    public PreprocessingLeakageElementVisitor(List<PreprocessingLeakageInstance> preprocessingLeakageInstances, @NotNull ProblemsHolder holder) {
        this.preprocessingLeakageInstances = preprocessingLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
               // super.visitElement(element);//TODO: do we need this?
                if (leakageIsAssociatedWithNode(preprocessingLeakageInstances, element)) {
                    holder.registerProblem(element, InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey()));
                }
            }
        };
    }

    @Override
    public Predicate<PreprocessingLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);
        return instance -> (instance.lineNumber() == nodeLineNumber)
                && Objects.equals(instance.test(), node.getText()); //TODO: make sure it's ok to have text and not name
    }

    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
//TODO: recursive
        if (leakageIsAssociatedWithNode(preprocessingLeakageInstances, node)) {
            holder.registerProblem(node, InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey()));

        }

    }
    @Override
    public void visitPyFunction(@NotNull PyFunction node){
        this.recursiveElementVisitor.visitElement(node);

    }

    @Override
    public void visitPyNamedParameter(@NotNull PyNamedParameter node) {

        if (leakageIsAssociatedWithNode(preprocessingLeakageInstances, node)) {
            holder.registerProblem(node, InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey()));

        }
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
                preprocessingLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().ifPresent(instance->   renderInspectionOnTaintWithCause(node, holder, instance.getLeakageSource().getCause(), keyword));


            }
        }
    }


    @Override
    public void renderInspectionOnLeakageSource(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<PreprocessingLeakageInstance> leakageInstances) {

        //TODO: change name?
        PreprocessingLeakageInstance leakageInstance = preprocessingLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().get();


        holder.registerProblem(node, getInspectionMessageForLeakageSource(leakageInstance, node));//TODO: rendering multiple messages on some leakage sources sometimes

    }

    @NotNull
    private static String getInspectionMessageForLeakageSource(PreprocessingLeakageInstance leakageInstance, @NotNull PyCallExpression node) {
        StringBuilder inspectionMessage = new StringBuilder(InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey()));
        inspectionMessage.append(" ");

        var cause = leakageInstance.getLeakageSource().getCause();
        inspectionMessage.append(InspectionBundle.get(cause.getInspectionTextKey()));

        return inspectionMessage.toString();
    }

}
