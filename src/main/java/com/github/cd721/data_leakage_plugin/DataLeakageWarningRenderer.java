package com.github.cd721.data_leakage_plugin;

import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import org.jetbrains.annotations.NotNull;

public abstract class DataLeakageWarningRenderer implements EditorCustomElementRenderer {
    public abstract boolean warningIsDisplayed();

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        return 50;
    }

  
}
