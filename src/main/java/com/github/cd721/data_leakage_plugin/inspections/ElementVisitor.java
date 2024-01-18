package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.OverlapLeakageSourceKeyword;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public abstract class ElementVisitor<T extends LeakageInstance> extends PyElementVisitor {
    public ProblemsHolder holder;

    public abstract Predicate<T> leakageAssociatedWithNode(@NotNull PyReferenceExpression node);

    public boolean leakageIsAssociatedWithNode(List<T> leakageInstances, @NotNull PyReferenceExpression node) {
        return leakageInstances.stream().anyMatch(leakageAssociatedWithNode(node));
    }

    public Predicate<T> leakageSourceAssociatedWithNode(@NotNull PyCallExpression node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

        return instance -> (instance.getLeakageSource().getLineNumbers().stream()
                .anyMatch(leakageSourceLineNumber -> leakageSourceLineNumber == nodeLineNumber));

    }

    public boolean leakageSourceIsAssociatedWithNode(List<T> leakageInstances, @NotNull PyCallExpression node) {
        return leakageInstances.stream().anyMatch(leakageSourceAssociatedWithNode(node));
    }

    @NotNull
    public List<T> getLeakageInstancesWhoseTaintsContainText(List<T> leakageInstances, String text) {
        return leakageInstances.stream().filter(instance -> instance.getLeakageSource().getTaints().stream().anyMatch(taint -> taint.containsText(text))).toList();
    }

    private void renderInspectionOnLeakageSource(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, String cause, String key) {
        if (node.getText().toLowerCase().contains(cause)) {//TODO: not the whole node text, just the method itself
            renderInspectionOnLeakageSource(node, holder, InspectionBundle.get(key));

        }
    }
    private void renderInspectionOnLeakageSource(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder,String text) {
            holder.registerProblem(node, text);


    }

    public void renderInspectionOnLeakageSourceForInstanceWithKeyword(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder,

                                                                      OverlapLeakageSourceKeyword keyword) {
    //    var leakageInstancesWithCertainCause = getLeakageInstancesWhoseTaintsContainText(leakageInstances, keyword.toString());

        renderInspectionOnLeakageSource(node, holder,  keyword.getTaintKeyword(), keyword.getCause().getInspectionTextKey());
    }

    public abstract void renderInspectionOnLeakageInstance(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances);

    public abstract void renderInspectionOnLeakageSources(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances);

}
