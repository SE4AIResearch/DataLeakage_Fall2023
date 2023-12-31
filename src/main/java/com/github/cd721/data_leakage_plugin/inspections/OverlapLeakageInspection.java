package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

public class OverlapLeakageInspection extends LeakageInspection<OverlapLeakageInstance> {

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }

    @Override
    public ElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {
        var leakageInstances = leakageAnalysisParser.LeakageInstances();

        var overlapLeakageInstances = getLeakageInstancesForType(leakageInstances);
        return new ElementVisitor() {
            @Override
            public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
                var nodeLineNumber = Utils.getNodeLineNumber(node, holder);

                Predicate<OverlapLeakageInstance> leakageAssociatedWithNode = instance -> (instance.lineNumber() == nodeLineNumber)
                        && Objects.equals(instance.test(), node.getName());

                if (overlapLeakageInstances.stream().anyMatch(leakageAssociatedWithNode)) {
                    holder.registerProblem(node, InspectionBundle.get("inspectionText.overlapLeakage.text"));
                }


            }

            @Override
            public void visitPyCallExpression(@NotNull PyCallExpression node) {
                var nodeLineNumber = Utils.getNodeLineNumber(node, holder);
                Predicate<OverlapLeakageInstance> leakageAssociatedWithNode = instance -> (instance.getLeakageSource().getLineNumber() == nodeLineNumber);


                if (overlapLeakageInstances.stream().anyMatch(leakageAssociatedWithNode)) {
                    holder.registerProblem(node, "Potential source of overlap leakage.");
                }


            }
        };
    }


}
