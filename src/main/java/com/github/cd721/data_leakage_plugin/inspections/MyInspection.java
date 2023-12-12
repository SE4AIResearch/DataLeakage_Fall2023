package com.github.cd721.data_leakage_plugin.inspections;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiBinaryFile;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.inspections.PyInspection;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MyInspection extends PyInspection {
    @Override
    public @NotNull PyElementVisitor buildVisitor(@NotNull ProblemsHolder holder,
                                                  boolean isOnTheFly,
                                                  @NotNull LocalInspectionToolSession session) {

        final PsiFile file = holder.getFile();
        if (InjectedLanguageManager.getInstance(file.getProject()).getInjectionHost(file) != null) return new PyElementVisitor();

        final Document document = file.getViewProvider().getDocument();
        if (document == null) return new PyElementVisitor();
        return new PyElementVisitor() {
            @Override
            public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
                if(Objects.equals(node.getName(), "X_test")) {
                            holder.registerProblem(node,"This is a reference expression.");
                }
            }
        };

    }

}
