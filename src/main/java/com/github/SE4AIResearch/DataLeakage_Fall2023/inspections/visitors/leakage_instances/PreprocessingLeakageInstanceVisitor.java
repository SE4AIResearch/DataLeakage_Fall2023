package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class PreprocessingLeakageInstanceVisitor extends InstanceElementVisitor<PreprocessingLeakageInstance> {
    private final List<PreprocessingLeakageInstance> preprocessingLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;

    private final PreprocessingLeakageQuickFix myQuickFix = new PreprocessingLeakageQuickFix();

    public PreprocessingLeakageInstanceVisitor(List<PreprocessingLeakageInstance> preprocessingLeakageInstances, @NotNull ProblemsHolder holder) {
        this.preprocessingLeakageInstances = preprocessingLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageInstance(preprocessingLeakageInstances, element, myQuickFix);
            }
        };
    }

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.PreprocessingLeakage;
    }

    @Override
    public Predicate<PreprocessingLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);
        return instance -> (instance.lineNumber() == nodeLineNumber)
                && (instance.variableName().contains(node.getText())); //TODO: make sure it's ok to have text and not name
    }

    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
//      var log=  Logger.getInstance(PreprocessingLeakageInstanceVisitor.class);
//        log.warn("HERE***********************************************************");
        renderInspectionOnLeakageInstance(preprocessingLeakageInstances, node, myQuickFix);

    }


    @Override
    public void visitPyNamedParameter(@NotNull PyNamedParameter node) {

        if (leakageIsAssociatedWithNode(preprocessingLeakageInstances, node)) {
            holder.registerProblem(node, InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey()), ProblemHighlightType.WARNING, myQuickFix);

        }
    }

    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }

    private class PreprocessingLeakageQuickFix implements LocalQuickFix {

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

            int offset = document.getLineStartOffset(lineNumber);

            @Nullable
            PsiElement statement = psiFile.findElementAt(offset);
            //won't work if assignment is split on multiple lines

            document.insertString(offset, "split()\n");


        }
    }

}
