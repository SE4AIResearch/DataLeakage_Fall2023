package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * A type of {@link PyElementVisitor} that visits different types of elements within the PSI tree,
 * such as {@link PyReferenceExpression}s.
 */
public class OverlapLeakageInstanceVisitor extends InstanceElementVisitor<OverlapLeakageInstance> {
    private final List<OverlapLeakageInstance> overlapLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;
    private final OverlapLeakageQuickFix myQuickFix = new OverlapLeakageQuickFix();

    public OverlapLeakageInstanceVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = overlapLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageInstance(overlapLeakageInstances, element, myQuickFix);
            }
        };
    }


    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }

    @Override
    public Predicate<OverlapLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

        return instance -> (instance.lineNumber() == nodeLineNumber) &&
                (instance.variableName().contains(node.getText())
                        || (instance.train().contains(node.getText())));
        //Objects.equals(instance.test(), node.getText()); //TODO: make sure it's ok to have text and not name

    }

    public List<OverlapLeakageInstance> getLeakageInstances() {
        return overlapLeakageInstances;
    }

    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {

        renderInspectionOnLeakageInstance(overlapLeakageInstances, node, myQuickFix);
    }

    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }


    private class OverlapLeakageQuickFix implements LocalQuickFix {


        @NotNull
        @Override
        public String getName() {
            return InspectionBundle.get("inspectionText.swapSplitAndSample.quickfix.text");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {


        }
    }

}
