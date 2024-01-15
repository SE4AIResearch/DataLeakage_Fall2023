package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A type of {@link PyElementVisitor} that visits different types of elements within the PSI tree,
 * such as {@link PyReferenceExpression}s.
 */
public class OverlapLeakageElementVisitor extends ElementVisitor<OverlapLeakageInstance> {
    private List<OverlapLeakageInstance> overlapLeakageInstances;


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
            holder.registerProblem(node, InspectionBundle.get("inspectionText.overlapLeakage.text"));


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


    public void renderInspectionOnLeakageInstance(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {

        //TODO: refactor
        OverlapLeakageInstance leakageInstance = overlapLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().get();


        //TODO: what can we use besides getCallee?
        var matchingTaint = leakageInstance.findTaintThatMatchesText(node.getCallee().getText());

        String appendmsg = "";
        if (matchingTaint.containsText("sample")) {
            appendmsg = InspectionBundle.get("inspectionText.sampleAfterSplitReminder.text");
        } else if (matchingTaint.containsText("flow")) {//TODO: should just be the text on the right side of the period, not the whole thing
            appendmsg = InspectionBundle.get("inspectionText.dataAugmentationWarning.text");
        }

        holder.registerProblem(node, InspectionBundle.get("inspectionText.overlapLeakageSource.text") + " " + appendmsg);
    }

    private void renderInspectionOnLeakageSources(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {
        List<OverlapLeakageInstance> instancesWhoseSourcesHaveSampling = getTaintsThatContainText(overlapLeakageInstances, "sample");

        renderInspectionOnLeakageSource(instancesWhoseSourcesHaveSampling, node, "split", holder, "inspectionText.splitBeforeSampleReminder.text");

        List<OverlapLeakageInstance> instancesWhoseSourcesHaveDataAugmentation = getTaintsThatContainText(overlapLeakageInstances, "flow");

        renderInspectionOnLeakageSource(instancesWhoseSourcesHaveDataAugmentation, node, "flow", holder, "inspectionText.dataAugmentationWarning.text");

    }


}
