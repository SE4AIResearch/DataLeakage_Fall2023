package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.data.taints.Taint;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.OverlapLeakageSourceKeyword;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A type of {@link PyElementVisitor} that visits different types of elements within the PSI tree,
 * such as {@link PyReferenceExpression}s.
 */
public class OverlapLeakageElementVisitor extends ElementVisitor<OverlapLeakageInstance> {
    private final List<OverlapLeakageInstance> overlapLeakageInstances;


    @Override
    public Predicate<OverlapLeakageInstance> leakageAssociatedWithNode(@NotNull PyReferenceExpression node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

        return instance -> (instance.lineNumber() == nodeLineNumber) && Objects.equals(instance.test(), node.getName());
    }

    public OverlapLeakageElementVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = overlapLeakageInstances;
        this.holder = holder;
    }


    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {


        if (leakageIsAssociatedWithNode(overlapLeakageInstances, node)) {
            holder.registerProblem(node, InspectionBundle.get(LeakageType.OverlapLeakage.getInspectionTextKey()));


        }
    }


    //TODO: consider different making different visitors for performance
    @Override
    public void visitPyCallExpression(@NotNull PyCallExpression node) {//TODO: consider moving this into visitPyReferenceExpression.. will require some refactoring.


        //TODO: extract
        if (leakageSourceIsAssociatedWithNode(overlapLeakageInstances, node)) {

            renderInspectionOnLeakageInstance(node, holder, overlapLeakageInstances);
        }

        renderInspectionOnLeakageSources(node, holder, overlapLeakageInstances);

    }


    @Override
    public void renderInspectionOnLeakageInstance(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {
//TODO: change name?
        OverlapLeakageInstance leakageInstance = overlapLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().get();

        //TODO: what can we use besides getCallee?
        var taintAssociatedWithLeakageInstance = leakageInstance.findTaintThatMatchesText(node.getCallee().getText());

        holder.registerProblem(node, getInspectionMessageForLeakageInstance(taintAssociatedWithLeakageInstance));
    }

    @NotNull
    private static String getInspectionMessageForLeakageInstance(Taint taintAssociatedWithLeakageInstance) {
        StringBuilder inspectionMessage = new StringBuilder(InspectionBundle.get(LeakageType.OverlapLeakage.getInspectionTextKey()));
        inspectionMessage.append(" ");

        //get method keyword associated with taint
        Arrays.stream(OverlapLeakageSourceKeyword.values()).filter(value -> taintAssociatedWithLeakageInstance.containsText(value.toString()))//TODO: should just be the text on the right side of the period, not the whole thing
                .findFirst().ifPresent(keyword -> inspectionMessage.append(InspectionBundle.get(keyword.getCause().getInspectionTextKey())));

        return inspectionMessage.toString();
    }

    @Override
    public void renderInspectionOnLeakageSources(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {
        renderInspectionOnLeakageSourceForInstanceWithKeyword(node, holder, overlapLeakageInstances, OverlapLeakageSourceKeyword.sample);
        //   instancesWhoseSourcesHaveSampling

        renderInspectionOnLeakageSourceForInstanceWithKeyword(node, holder, overlapLeakageInstances, OverlapLeakageSourceKeyword.flow);
//instancesWhoseSourcesHaveDataAugmentation
    }


}
