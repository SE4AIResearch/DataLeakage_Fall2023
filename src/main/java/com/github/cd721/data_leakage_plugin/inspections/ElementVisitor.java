package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageSourceKeyword;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public abstract class ElementVisitor<T extends LeakageInstance, U extends LeakageSourceKeyword> extends PyElementVisitor {
    public ProblemsHolder holder;

    public abstract Predicate<T> leakageInstanceIsAssociatedWithNode(@NotNull PyReferenceExpression node);
    public abstract void renderInspectionOnLeakageInstance(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<T> leakageInstances);


    public boolean leakageIsAssociatedWithNode(List<T> leakageInstances, @NotNull PyReferenceExpression node) {
        return leakageInstances.stream().anyMatch(leakageInstanceIsAssociatedWithNode(node));
    }

    public Predicate<T> leakageSourceAssociatedWithNode(@NotNull PyCallExpression node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

        return instance -> (instance.getLeakageSource().getLineNumbers().stream()
                .anyMatch(leakageSourceLineNumber -> leakageSourceLineNumber == nodeLineNumber));

    }

    public boolean leakageSourceIsAssociatedWithNode(List<T> leakageInstances, @NotNull PyCallExpression node) {
        return leakageInstances.stream().anyMatch(leakageSourceAssociatedWithNode(node));
    }

    public void renderInspectionOnLeakageSourceForInstanceWithKeyword(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder,
                                                                      U keyword) {
        //    var leakageInstancesWithCertainCause = getLeakageInstancesWhoseTaintsContainText(leakageInstances, keyword.toString());

        var taintKeyword = keyword.getTaintKeyword();
        var key = keyword.getCause().getInspectionTextKey();

        if (node.getText().toLowerCase().contains(taintKeyword)) {//TODO: not the whole node text, just the method itself
            holder.registerProblem(node, InspectionBundle.get(key));
        }
    }

    public void renderInspectionOnLeakageSources(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder,
                                                 List<U> keywords) {
        // for overlap leakage instancesWhoseSourcesHaveDataAugmentation, instancesWhoseSourcesHaveSampling

        keywords.forEach(keyword -> renderInspectionOnLeakageSourceForInstanceWithKeyword(node, holder, keyword));

    }



}
