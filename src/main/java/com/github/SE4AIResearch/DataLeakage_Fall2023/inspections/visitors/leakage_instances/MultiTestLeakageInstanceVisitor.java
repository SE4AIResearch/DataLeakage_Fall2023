package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.MultiTestLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.Refactoring;
import com.intellij.refactoring.RefactoringFactory;
import com.jetbrains.python.psi.PyPsiFacade;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class MultiTestLeakageInstanceVisitor extends InstanceElementVisitor<MultiTestLeakageInstance> {
    private final List<MultiTestLeakageInstance> multiTestLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;
    private final MultiTestLeakageQuickFix myQuickFix = new MultiTestLeakageQuickFix();

    public MultiTestLeakageInstanceVisitor(List<MultiTestLeakageInstance> multiTestLeakageInstances, @NotNull ProblemsHolder holder) {
        this.multiTestLeakageInstances = multiTestLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageInstance(multiTestLeakageInstances, element);
            }
        };
    }

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.MultiTestLeakage;
    }

    @Override
    public Predicate<MultiTestLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {

        return instance -> (instance.lineNumber() == PsiUtils.getNodeLineNumber(node, holder))
                && Objects.equals(instance.test(), node.getText()
        );

    }

    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {

        renderInspectionOnLeakageInstance(multiTestLeakageInstances, node, myQuickFix);
    }

    private class MultiTestLeakageQuickFix implements LocalQuickFix {
        @NotNull
        @Override
        public String getName() {
            return InspectionBundle.get("inspectionText.removeRedundantTestEvaluations.quickfix.text");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            var myFacade = PyPsiFacade.getInstance(project);
            var instance = descriptor.getPsiElement();
            //  var references = instance.getReferences();
            //does not get all references
//            PsiElement parentOfType = PsiTreeUtil.getParentOfType(instance, instance.getClass());
//var references = ReferencesSearch.search(parentOfType);
            var myFactory = RefactoringFactory.getInstance(project);
            var myRefactoring = myFactory.createRename((PsiElement) instance, instance.getText() + "_1", true, true);
//            var usages = myRefactoring.findUsages();
//            myRefactoring.doRefactoring(usages);

            var descriptionText = descriptor.getDescriptionTemplate();
            var psiElement = descriptor.getPsiElement();
            var psiFile = psiElement.getContainingFile();
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            Document document = documentManager.getDocument(psiFile);
            var lineNumber = descriptor.getLineNumber();

            int offset = document.getLineStartOffset(lineNumber);
            document.replaceString(instance.getTextOffset(), instance.getTextOffset()+instance.getTextLength(), instance.getText()+"_1");

        }
    }
}
