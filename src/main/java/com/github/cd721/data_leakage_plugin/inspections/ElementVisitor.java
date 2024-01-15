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
public class ElementVisitor extends PyElementVisitor {
    private List<OverlapLeakageInstance> overlapLeakageInstances;
    private ProblemsHolder holder;

    public ElementVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = overlapLeakageInstances;
        this.holder= holder;
    }
    public ElementVisitor(){

    }

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
    public void visitPyCallExpression(@NotNull PyCallExpression node) {//TODO: consider moving this into visitPyReferenceExpression.. will require some refactoring.
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

        Predicate<OverlapLeakageInstance> leakageSourceAssociatedWithNode =
                instance -> (instance.getLeakageSource().getLineNumbers().stream().anyMatch(leakageSourceLineNumber ->
                        leakageSourceLineNumber == nodeLineNumber));


        //TODO: extract
        if (overlapLeakageInstances.stream().anyMatch(leakageSourceAssociatedWithNode)) {
            renderInspectionOnLeakageInstance(node, holder, leakageSourceAssociatedWithNode, overlapLeakageInstances);
        }

        renderInspectionOnLeakageSources(node, holder, overlapLeakageInstances);

    }
    private static void renderInspectionOnLeakageInstance(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, Predicate<OverlapLeakageInstance> leakageAssociatedWithNode, List<OverlapLeakageInstance> overlapLeakageInstances) {
        //TODO: refactor
        OverlapLeakageInstance leakageInstance = overlapLeakageInstances.stream().filter(leakageAssociatedWithNode).findFirst().get();


        //TODO: what can we use besides getCallee?
        var matchingTaint = leakageInstance.findTaintThatMatchesText(node.getCallee().getText());

        String appendmsg = "";
        if(matchingTaint.containsText("sample")){
            appendmsg = InspectionBundle.get("inspectionText.sampleAfterSplitReminder.text");
        } else if (matchingTaint.containsText("flow")){//TODO: should just be the text on the right side of the period, not the whole thing
            appendmsg = InspectionBundle.get("inspectionText.dataAugmentationWarning.text");
        }

        holder.registerProblem(node, InspectionBundle.get("inspectionText.overlapLeakageSource.text") + " " +appendmsg);
    }

    private static void renderInspectionOnLeakageSources(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {
        List<OverlapLeakageInstance> leakageSourcesWithSampling = getTaintsThatContainText(overlapLeakageInstances, "sample");

        renderInspectionOnLeakageSource(leakageSourcesWithSampling, node, "split", holder, "inspectionText.splitBeforeSampleReminder.text");

        List<OverlapLeakageInstance> leakageSourcesWithDataAugmentation = getTaintsThatContainText(overlapLeakageInstances, "flow");

        renderInspectionOnLeakageSource(leakageSourcesWithDataAugmentation, node, "flow", holder, "inspectionText.dataAugmentationWarning.text");

    }

    private static void renderInspectionOnLeakageSource(List<OverlapLeakageInstance> leakageSourcesWithDataAugmentation, @NotNull PyCallExpression node, String flow, @NotNull ProblemsHolder holder, String key) {
        if (!leakageSourcesWithDataAugmentation.isEmpty() && node.getText().toLowerCase().contains(flow)) {//TODO: not the whole node text, just the method itself
            holder.registerProblem(node, InspectionBundle.get(key));

        }
    }

    @NotNull
    private static List<OverlapLeakageInstance> getTaintsThatContainText(List<OverlapLeakageInstance> overlapLeakageInstances, String flow) {
        return overlapLeakageInstances.stream().filter(
                instance -> instance.getLeakageSource().getTaints().stream()
                        .anyMatch(taint -> taint.containsText(flow))
        ).toList();
    }
}
