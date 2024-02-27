package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;


import com.github.SE4AIResearch.DataLeakage_Fall2023.data.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.LeakageQuickFix;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class PreprocessingLeakageQuickFix extends LeakageQuickFix<PreprocessingLeakageInstance> {
    private final List preprocessingLeakageInstances;

    protected PreprocessingLeakageQuickFix(List<PreprocessingLeakageInstance> instances) {
        this.preprocessingLeakageInstances = instances;
    }

    @NotNull
    @Override
    public String getName() {
        return InspectionBundle.get("inspectionText.vectorizingTextData.quickfix.text");
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

        //Source not linked to instance

        //Sample
        if (descriptionText.equals(InspectionBundle.get("inspectionText.preprocessingLeakage.text"))
                && psiElement.getText().contains(OverlapLeakageSourceKeyword.sample.toString())) {

        }

//won't work if assignment is split on multiple lines
        var instance = getLeakageInstanceAssociatedWithNode(preprocessingLeakageInstances, psiElement);
        var source = instance.getLeakageSource();
        if (source.getCause().equals(LeakageCause.VectorizingTextData)) {

            int offset = document.getLineStartOffset(lineNumber);

            @Nullable
            PsiElement statement = psiFile.findElementAt(offset
            );
            //won't work if assignment is split on multiple lines

            document.insertString(offset, "split()\n");

        }


    }
}
