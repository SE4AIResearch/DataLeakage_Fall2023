package com.github.cd721.data_leakage_plugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class DataLeakageIndicator {
    private final DataLeakageWarningRendererFactory dataLeakageWarningRendererFactory;

    public DataLeakageIndicator() {
        dataLeakageWarningRendererFactory = new DataLeakageWarningRendererFactory();
    }


    public void renderDataLeakageWarning(Editor editor, int lineNumber, LeakageType leakageType) {
        DataLeakageWarningRenderer dataLeakageWarningRenderer = dataLeakageWarningRendererFactory.GetRendererForLeakageType(leakageType);
        if (!dataLeakageWarningRenderer.warningIsDisplayed()) {
            if (editor != null) {
                int offset = editor.visualLineToY(lineNumber);
                editor.getInlayModel().addBlockElement(offset, false, true, 1, dataLeakageWarningRenderer);

            }
        }

    }

    public void clearAllDataLeakageWarnings(Editor editor) {
        disposeInlays(editor);
    }


    private static void disposeInlays(Editor editor) {
        for (var i : editor.getInlayModel().getBlockElementsInRange(0, editor.getDocument().getTextLength())) {
            i.dispose();
        }
    }


}

