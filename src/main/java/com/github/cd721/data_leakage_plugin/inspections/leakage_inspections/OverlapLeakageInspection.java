package com.github.cd721.data_leakage_plugin.inspections.leakage_inspections;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.inspections.ElementVisitor;
import com.github.cd721.data_leakage_plugin.inspections.InspectionBundle;
import com.github.cd721.data_leakage_plugin.inspections.PsiUtils;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
                var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

                Predicate<OverlapLeakageInstance> leakageAssociatedWithNode = instance -> (instance.lineNumber() == nodeLineNumber)
                        && Objects.equals(instance.test(), node.getName());

                if (overlapLeakageInstances.stream().anyMatch(leakageAssociatedWithNode)) {
                    holder.registerProblem(node, InspectionBundle.get("inspectionText.overlapLeakage.text"));
                }


            }

            //TODO: consider different making different visitors for performance
            @Override
            public void visitPyCallExpression(@NotNull PyCallExpression node) {
                var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

                Predicate<OverlapLeakageInstance> leakageAssociatedWithNode =
                        instance -> (instance.getLeakageSource().getLineNumbers().stream().anyMatch(leakageSourceLineNumber ->
                                leakageSourceLineNumber == nodeLineNumber));


                //TODO: extract
                if (overlapLeakageInstances.stream().anyMatch(leakageAssociatedWithNode)) {
                    renderInspectionOnLeakageInstance(node,holder, leakageAssociatedWithNode,overlapLeakageInstances);
                }

                renderInspectionOnSplitFunction(node,holder,overlapLeakageInstances);

            }
        };
    }

    private static void renderInspectionOnLeakageInstance(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, Predicate<OverlapLeakageInstance> leakageAssociatedWithNode, List<OverlapLeakageInstance> overlapLeakageInstances) {
        //TODO: refactor
        OverlapLeakageInstance leakageInstance = overlapLeakageInstances.stream().filter(leakageAssociatedWithNode).findFirst().get();


        //TODO: what can we use besides getCallee?
        var matchingTaint = leakageInstance.findTaintThatMatchesText(node.getCallee().getText());

        String appendmsg = "";
        if(matchingTaint.containsText("sample")){
            appendmsg = InspectionBundle.get("inspectionText.sampleAfterSplitReminder.text");
        }

        holder.registerProblem(node, InspectionBundle.get("inspectionText.overlapLeakageSource.text") + " " +appendmsg);
    }

    private static void renderInspectionOnSplitFunction(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {
        List<OverlapLeakageInstance> leakageSourcesWithSampling = overlapLeakageInstances.stream().filter(
                instance -> instance.getLeakageSource().getTaints().stream()
                        .anyMatch(taint -> taint.containsText("sample"))
        ).toList();

        if(!leakageSourcesWithSampling.isEmpty() && node.getText().toLowerCase().contains("split")){
            holder.registerProblem(node,InspectionBundle.get("inspectionText.splitBeforeSampleReminder.text") );

        }
    }


}
