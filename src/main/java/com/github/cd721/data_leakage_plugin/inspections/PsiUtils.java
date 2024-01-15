package com.github.cd721.data_leakage_plugin.inspections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.jetbrains.python.psi.PyExpression;
import org.jetbrains.annotations.NotNull;

public class PsiUtils {

    public static PsiFile getFile(@NotNull ProblemsHolder holder) {
        return holder.getFile();
    }

    public static PsiLanguageInjectionHost getInjectionHost(@NotNull ProblemsHolder holder) {
        var file = getFile(holder);
        return InjectedLanguageManager.getInstance(file.getProject()).getInjectionHost(file);
    }

    public static Document getDocument(@NotNull ProblemsHolder holder) {
        return getFile(holder).getViewProvider().getDocument();
    }

    public static int getNodeLineNumber(PyExpression node, @NotNull ProblemsHolder holder) {
        var offset = node.getTextOffset();
        return getDocument(holder).getLineNumber(offset) + 1; //getLineNumber is zero-based, must add 1
    }

}
