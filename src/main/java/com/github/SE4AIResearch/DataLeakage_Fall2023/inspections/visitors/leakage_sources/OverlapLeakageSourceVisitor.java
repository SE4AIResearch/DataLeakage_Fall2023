package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.psi.*;
import java.util.Arrays;
import java.util.List;

/**
 * A type of {@link PyElementVisitor} that visits different types of elements within the PSI tree,
 * such as {@link PyReferenceExpression}s.
 */
public class OverlapLeakageSourceVisitor extends SourceElementVisitor<OverlapLeakageInstance, OverlapLeakageSourceKeyword> {
    private final List<OverlapLeakageInstance> overlapLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;

    private final OverlapLeakageQuickFix myQuickFix = new OverlapLeakageQuickFix();

    public OverlapLeakageSourceVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = overlapLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageSource(element, holder, overlapLeakageInstances, myQuickFix);
            }
        };
    }


    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }


    //TODO: consider different making different visitors for performance
    @Override
    public void visitPyCallExpression(@NotNull PyCallExpression node) {//TODO: consider moving this into visitPyReferenceExpression.. will require some refactoring.

        //TODO: extract

        if (!overlapLeakageInstances.isEmpty()) {
            if (leakageSourceIsAssociatedWithNode(overlapLeakageInstances, node)) {

                renderInspectionOnLeakageSource(node, holder, overlapLeakageInstances,myQuickFix);
            }

            renderInspectionOnTaints(node, holder, Arrays.stream(OverlapLeakageSourceKeyword.values()).toList());
        }
    }


    @Override
    public void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {
//TODO: change name?
        overlapLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().ifPresent(
                instance -> holder.registerProblem(node, getInspectionMessageForLeakageSource(instance.getLeakageSource().findTaintThatMatchesText(node.getFirstChild().getText())), ProblemHighlightType.WARNING)
        );


    }

    public void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances, LocalQuickFix fix) {
//TODO: change name?
        overlapLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().ifPresent(
                instance -> holder.registerProblem(node, getInspectionMessageForLeakageSource(instance.getLeakageSource().findTaintThatMatchesText(node.getFirstChild().getText())), ProblemHighlightType.WARNING, fix)
        );


    }

    @NotNull
    private static String getInspectionMessageForLeakageSource(Taint taintAssociatedWithLeakageInstance) {
        StringBuilder inspectionMessage = new StringBuilder(InspectionBundle.get(LeakageType.OverlapLeakage.getInspectionTextKey()));
        inspectionMessage.append(" ");

        //get method keyword associated with taint
        Arrays.stream(OverlapLeakageSourceKeyword.values()).filter(value -> taintAssociatedWithLeakageInstance.containsText(value.toString()))//TODO: should just be the text on the right side of the period, not the whole thing
                .findFirst().ifPresent(keyword -> inspectionMessage.append(InspectionBundle.get(keyword.getPotentialCauses().get(0).getInspectionTextKey())));//TODO: refactor?

        return inspectionMessage.toString();
    }

    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }

    private class OverlapLeakageQuickFix implements LocalQuickFix {
//private final List<OverlapLeakageInstance> overlapLeakageInstances;

//        public OverlapLeakageQuickFix(List<OverlapLeakageInstance> overlapLeakageInstances) {
//
//            this.overlapLeakageInstances = overlapLeakageInstances;
//        }

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

            var lineNumber = descriptor.getLineNumber();

            var descriptionText = descriptor.getDescriptionTemplate();
            var psiElement = descriptor.getPsiElement();
            var psiFile = psiElement.getContainingFile();
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            Document document = documentManager.getDocument(psiFile);
            //Split
            if (descriptionText.equals(InspectionBundle.get("inspectionText.splitBeforeSampleReminder.text"))) {

            }
            //Source not linked to instance

            //Sample
            if (descriptionText.equals(InspectionBundle.get("inspectionText.overlapLeakage.text"))
                    && psiElement.getText().contains(OverlapLeakageSourceKeyword.sample.toString())) {

            }

//won't work if assignment is split on multiple lines
            var instance = getInstanceForLeakageSourceAssociatedWithNode(overlapLeakageInstances, psiElement);
            var source = instance.getLeakageSource();
            if (source.getCause().equals(LeakageCause.SplitBeforeSample)) {
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

                int offset = document.getLineStartOffset(lineNumber);

                @Nullable
                PsiElement firstElementOnLine = psiFile.findElementAt(offset
                );
                PsiManager manager = PsiManager.getInstance ( project );
              var myFacade=  PyPsiFacade.getInstance(project);


                document.insertString(offset, "split()\n");


            }


        }
    }


}
