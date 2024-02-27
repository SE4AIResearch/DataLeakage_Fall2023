package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.MultiTestLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.find.FindManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.Refactoring;
import com.intellij.refactoring.RefactoringFactory;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.util.DocumentUtil;
import com.jetbrains.python.psi.PyPsiFacade;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.refactoring.rename.RenamePyElementProcessor;
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
    public List<MultiTestLeakageInstance> getLeakageInstances(){
        return multiTestLeakageInstances;
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


}
