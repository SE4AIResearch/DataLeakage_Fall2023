package com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageCause;
import com.github.cd721.data_leakage_plugin.enums.LeakageSourceKeyword;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.inspections.InspectionBundle;
import com.github.cd721.data_leakage_plugin.inspections.PsiUtils;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public abstract class SourceElementVisitor<T extends LeakageInstance, U extends LeakageSourceKeyword> extends PyElementVisitor {
    public ProblemsHolder holder;

    public abstract LeakageType getLeakageType();


    public abstract void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<T> leakageInstances);



    public Predicate<T> leakageSourceAssociatedWithNode(@NotNull PyCallExpression node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

        return instance -> (instance.getLeakageSource().getLineNumbers().stream()
                .anyMatch(leakageSourceLineNumber -> leakageSourceLineNumber == nodeLineNumber));

    }

    public boolean leakageSourceIsAssociatedWithNode(List<T> leakageInstances, @NotNull PyCallExpression node) {
        return leakageInstances.stream().anyMatch(leakageSourceAssociatedWithNode(node));
    }

    public void renderInspectionOnTaintForInstanceWithKeyword(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder,
                                                              U keyword) {

        var taintKeyword = keyword.getTaintKeyword();
        var potentialCauses = keyword.getPotentialCauses();

        var key = potentialCauses.get(0).getInspectionTextKey();//TODO: refactor?

        if (node.getText().toLowerCase().contains(taintKeyword)) {//TODO: not the whole node text, just the method itself
            holder.registerProblem(node, InspectionBundle.get(key));
        }
    }

    public void renderInspectionOnTaintWithCause(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, LeakageCause cause, U keyword) {
        var taintKeyword = keyword.getTaintKeyword();
        var key = cause.getInspectionTextKey();

        if (node.getText().toLowerCase().contains(taintKeyword)) {//TODO: not the whole node text, just the method itself
            holder.registerProblem(node, InspectionBundle.get(key));
        }
    }

    public void renderInspectionOnTaints(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder,
                                         List<U> keywords) {
        // for overlap leakage instancesWhoseSourcesHaveDataAugmentation, instancesWhoseSourcesHaveSampling

        keywords.forEach(keyword -> renderInspectionOnTaintForInstanceWithKeyword(node, holder, keyword));

    }




}
