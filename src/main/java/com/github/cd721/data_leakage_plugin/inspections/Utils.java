package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Utils {

    public static PsiFile getFile(@NotNull ProblemsHolder holder) {
        final PsiFile file = holder.getFile();
        return file;
    }

    public static PsiLanguageInjectionHost getInjectionHost(@NotNull ProblemsHolder holder) {
        var file = getFile(holder);
        return InjectedLanguageManager.getInstance(file.getProject()).getInjectionHost(file);


    }

    public static Document getDocument(@NotNull ProblemsHolder holder) {

        final Document document = getFile(holder).getViewProvider().getDocument();
        return document;
    }

    public static int getNodeLineNumber(PyReferenceExpression node,@NotNull ProblemsHolder holder) {
        var offset = node.getTextOffset();
        var nodeLineNumber = getDocument(holder).getLineNumber(offset) + 1; //getLineNumber is zero-based, must add 1
return nodeLineNumber;
    }




}
