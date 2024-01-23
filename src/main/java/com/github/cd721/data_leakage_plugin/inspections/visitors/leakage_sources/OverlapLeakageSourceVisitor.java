package com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.data.taints.Taint;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.OverlapLeakageSourceKeyword;
import com.github.cd721.data_leakage_plugin.inspections.InspectionBundle;
import com.github.cd721.data_leakage_plugin.inspections.PsiUtils;
import com.github.cd721.data_leakage_plugin.inspections.visitors.ElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyFunction;
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
public class OverlapLeakageSourceVisitor extends SourceElementVisitor<OverlapLeakageInstance, OverlapLeakageSourceKeyword> {
    private final List<OverlapLeakageInstance> overlapLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;


    public OverlapLeakageSourceVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = overlapLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:

            }
        };
    }


    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }





    //TODO: consider different making different visitors for performance
    @Override
    public void visitPyCallExpression(@NotNull PyCallExpression node) {//TODO: consider moving this into visitPyReferenceExpression.. will require some refactoring.

        //TODO: extract

        if (!overlapLeakageInstances.isEmpty()) {
            if (leakageSourceIsAssociatedWithNode(overlapLeakageInstances, node)) {

                renderInspectionOnLeakageSource(node, holder, overlapLeakageInstances);
            }

            renderInspectionOnTaints(node, holder, Arrays.stream(OverlapLeakageSourceKeyword.values()).toList());
        }
    }


    @Override
    public void renderInspectionOnLeakageSource(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {
//TODO: change name?
        OverlapLeakageInstance leakageInstance = overlapLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().get();

        var taintAssociatedWithLeakageInstance = leakageInstance.getLeakageSource().findTaintThatMatchesText(node.getFirstChild().getText());

        holder.registerProblem(node, getInspectionMessageForLeakageSource(taintAssociatedWithLeakageInstance));
    }

    @NotNull
    private static String getInspectionMessageForLeakageSource(Taint taintAssociatedWithLeakageInstance) {
        StringBuilder inspectionMessage = new StringBuilder(InspectionBundle.get(LeakageType.OverlapLeakage.getInspectionTextKey()));
        inspectionMessage.append(" ");

        //get method keyword associated with taint
        Arrays.stream(OverlapLeakageSourceKeyword.values()).filter(value -> taintAssociatedWithLeakageInstance.containsText(value.toString()))//TODO: should just be the text on the right side of the period, not the whole thing
                .findFirst().ifPresent(keyword -> inspectionMessage.append(InspectionBundle.get(keyword.getPotentialCauses().get(0).getInspectionTextKey())));//TODO: refactor?

        return inspectionMessage.toString();
    }


}
