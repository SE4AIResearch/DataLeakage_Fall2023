package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources;


import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.jetbrains.python.psi.PyPsiFacade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

 class OverlapLeakageQuickFix implements LocalQuickFix {


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
            PsiManager manager = PsiManager.getInstance(project);
            var myFacade = PyPsiFacade.getInstance(project);


            document.insertString(offset, "split()\n");


        }


    }
}
