package com.github.cd721.data_leakage_plugin.inspections.leakage_inspections;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.data.MultiTestLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.inspections.InspectionBundle;
import com.github.cd721.data_leakage_plugin.inspections.PsiUtils;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class MultiTestLeakageInspection extends LeakageInspection<MultiTestLeakageInstance> {

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.MultiTestLeakage;
    }


    public PyElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {
        return new PyElementVisitor() {
            @Override
            public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
                var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

                Predicate<MultiTestLeakageInstance> predicate = instance -> (instance.lineNumber() == nodeLineNumber)
                        && Objects.equals(instance.test(), node.getName()
                );
                var leakageInstances = leakageAnalysisParser.LeakageInstances();

                if (getLeakageInstancesForType(leakageInstances).stream().anyMatch(predicate)) {
                    holder.registerProblem(node, InspectionBundle.get("inspectionText.multiTestLeakage.text"));
                }

            }

            @Override
            public void visitPyCallExpression(@NotNull PyCallExpression node) {

            }
        };
    }

    private List<MultiTestLeakageInstance> getMultiTestLeakageInstances(List<LeakageInstance> leakageInstances) {
        return leakageInstances.stream()
                .filter(instance -> instance.type().equals(LeakageType.MultiTestLeakage))
                .map(instance -> ((MultiTestLeakageInstance) (instance))).toList();
    }


}