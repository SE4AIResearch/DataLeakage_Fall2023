package com.github.cd721.data_leakage_plugin.leakage_indicators;

import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Provider  implements InlayHintsProvider  {


    @Nullable
    @Override
    public InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new DataLeakageIndicator(editor);
    }
}
