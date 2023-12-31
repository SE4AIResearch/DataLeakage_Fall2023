package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.PreprocessingLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.parsers.LeakageAnalysisParser;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

public class PreprocessingLeakageInspection extends LeakageInspection<PreprocessingLeakageInstance> {
    private final LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.PreprocessingLeakage;
    }

    @Override
    public ElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {
        var leakageInstances = leakageAnalysisParser.LeakageInstances();

        var preprocessingLeakageInstances = getLeakageInstancesForType(leakageInstances);

        return new ElementVisitor() {
            @Override
            public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
                var nodeLineNumber =Utils.getNodeLineNumber(node, holder);
                Predicate<PreprocessingLeakageInstance> predicate = instance -> (instance.lineNumber() == nodeLineNumber)
                        && Objects.equals(instance.test(), node.getName());
                if (preprocessingLeakageInstances.stream().anyMatch(predicate)) {
                    holder.registerProblem(node, InspectionBundle.get("inspectionText.preprocessingLeakage.text"));
                }

            }

            @Override
            public void visitPyCallExpression(@NotNull PyCallExpression node) {

            }
        };
    }




}
