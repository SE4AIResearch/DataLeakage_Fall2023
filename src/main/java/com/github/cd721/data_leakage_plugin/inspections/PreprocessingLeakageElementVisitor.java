package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.PreprocessingLeakageInstance;
import com.github.cd721.data_leakage_plugin.data.taints.Taint;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.OverlapLeakageSourceKeyword;
import com.github.cd721.data_leakage_plugin.enums.PreprocessingLeakageSourceKeyword;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class PreprocessingLeakageElementVisitor extends ElementVisitor<PreprocessingLeakageInstance, PreprocessingLeakageSourceKeyword> {
    private final List<PreprocessingLeakageInstance> preprocessingLeakageInstances;

    public PreprocessingLeakageElementVisitor(List<PreprocessingLeakageInstance> preprocessingLeakageInstances, @NotNull ProblemsHolder holder) {
        this.preprocessingLeakageInstances = preprocessingLeakageInstances;
        this.holder = holder;
    }

    @Override
    public Predicate<PreprocessingLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PyExpression node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);
        return instance -> (instance.lineNumber() == nodeLineNumber)
                && Objects.equals(instance.test(), node.getName());
    }
    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {

        if (leakageIsAssociatedWithNode(preprocessingLeakageInstances, node)) {
            holder.registerProblem(node, InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey()));

        }
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
                PreprocessingLeakageInstance leakageInstance = preprocessingLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().get();

                renderInspectionOnTaintWithCause(node, holder, leakageInstance.getLeakageSource().getCause(), keyword);
            }
        }
    }


    @Override
    public void renderInspectionOnLeakageSource(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<PreprocessingLeakageInstance> leakageInstances) {

        //TODO: change name?
        PreprocessingLeakageInstance leakageInstance = preprocessingLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().get();



        holder.registerProblem(node, getInspectionMessageForLeakageSource(leakageInstance,node));

    }

    @NotNull
    private static String getInspectionMessageForLeakageSource(PreprocessingLeakageInstance leakageInstance,@NotNull PyCallExpression node) {
        StringBuilder inspectionMessage = new StringBuilder(InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey()));
        inspectionMessage.append(" ");

        //TODO: what can we use besides getCallee?
        var taintAssociatedWithLeakageInstance = leakageInstance.findTaintThatMatchesText(node.getCallee().getText());

        var cause = leakageInstance.getLeakageSource().getCause();
        inspectionMessage.append(cause.getInspectionTextKey());

        return inspectionMessage.toString();
    }

}
