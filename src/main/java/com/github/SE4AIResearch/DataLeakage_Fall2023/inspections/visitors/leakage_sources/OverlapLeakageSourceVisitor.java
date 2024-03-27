package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionUtils;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A type of {@link PyElementVisitor} that visits different types of elements within the PSI tree,
 * such as {@link PyReferenceExpression}s.
 */
public class OverlapLeakageSourceVisitor extends SourceElementVisitor<OverlapLeakageInstance, OverlapLeakageSourceKeyword> {
    private ProblemsHolder holder;
    private List<OverlapLeakageInstance> overlapLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;

    private final OverlapLeakageQuickFix myQuickFix = new OverlapLeakageQuickFix();

    Collection<RangeHighlighter> collection = new ArrayList<>();


    protected void removeInstance(OverlapLeakageInstance instance) {
        var newArr = new ArrayList<OverlapLeakageInstance>();
        var it = this.overlapLeakageInstances.iterator();
        while (it.hasNext()) {
            if (!it.next().equals(instance)) {
                newArr.add(it.next());

            }
        }
        this.overlapLeakageInstances = newArr;
    }

    public OverlapLeakageSourceVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = new ArrayList<>(overlapLeakageInstances);
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageSource(element, holder, overlapLeakageInstances, myQuickFix);
            }
        };
        //  this.myQuickFix = new OverlapLeakageQuickFix(overlapLeakageInstances);
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

                renderInspectionOnLeakageSource(node, holder, overlapLeakageInstances, myQuickFix);
            }

            renderInspectionOnTaints(node, holder, Arrays.stream(OverlapLeakageSourceKeyword.values()).toList());


        }
    }


    @Override
    public void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {


    }




    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }

    private class OverlapLeakageQuickFix implements LocalQuickFix {


        public OverlapLeakageQuickFix() {
        }

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

            var lineNumber = descriptor.getLineNumber() + 1;//was off by one

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

                int offset = document.getLineStartOffset(lineNumber - 1);

                @Nullable
                PsiElement firstElementOnLine = psiFile.findElementAt(offset
                );
                PsiManager manager = PsiManager.getInstance(project);
                var myFacade = PyPsiFacade.getInstance(project);

                var lineContentOfSplitCall = holder.getResults().stream().map(
                        problem -> problem.getPsiElement().getParent().getText()
                ).filter(taint -> taint.toLowerCase().contains("split")).findFirst().get();

                var offsetOfSplitCall = holder.getResults().stream().map(
                                problem -> problem.getPsiElement().getParent()
                        ).filter(taint -> taint.getText().toLowerCase().contains("split"))
                        .map(taint -> taint.getTextOffset()).filter(splitOffset -> splitOffset > offset).findFirst().get();

                document.replaceString(offsetOfSplitCall, offsetOfSplitCall +
                        lineContentOfSplitCall.length(), "");

                document.insertString(offset, lineContentOfSplitCall + "\n");


                //Remove split sample from leakage instances
                //  removeInstance(instance);

                var lineNumbersToRemove = new ArrayList<Integer>();
                lineNumbersToRemove.add(document.getLineNumber(offset));
                lineNumbersToRemove.add(document.getLineNumber(lineNumber - 1));
                lineNumbersToRemove.add(document.getLineNumber(offset) + 1);
                lineNumbersToRemove.add(document.getLineNumber(offsetOfSplitCall) + 1);
//                overlapLeakageInstances.stream().forEach(thisInstance ->
//                        thisInstance.getLeakageSource().removeLineNumbers(lineNumbersToRemove));

                InspectionUtils.addLinesToExclusion(lineNumbersToRemove);
                DaemonCodeAnalyzer.getInstance(project).restart();
            }


        }
    }
}

