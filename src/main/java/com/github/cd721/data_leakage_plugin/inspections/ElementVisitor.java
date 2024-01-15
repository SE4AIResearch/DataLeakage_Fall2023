package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
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
    public List<T> getTaintsThatContainText(List<T> leakageInstances, String text) {
        return leakageInstances.stream().filter(instance -> instance.getLeakageSource().getTaints().stream().anyMatch(taint -> taint.containsText(text))).toList();
    }

    public void renderInspectionOnLeakageSource(List<OverlapLeakageInstance> instances, @NotNull PyCallExpression node, String text, @NotNull ProblemsHolder holder, String key) {
        if (!instances.isEmpty() && node.getText().toLowerCase().contains(text)) {//TODO: not the whole node text, just the method itself
            holder.registerProblem(node, InspectionBundle.get(key));

        }
    }

}
