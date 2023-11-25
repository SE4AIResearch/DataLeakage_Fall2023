package com.github.cd721.data_leakage_plugin.leakage_indicators;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.warning_renderers.DataLeakageWarningRenderer;
import com.github.cd721.data_leakage_plugin.warning_renderers.DataLeakageWarningRendererFactory;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;


public class DataLeakageIndicator {
    private final DataLeakageWarningRendererFactory dataLeakageWarningRendererFactory;

    public DataLeakageIndicator() {
        dataLeakageWarningRendererFactory = new DataLeakageWarningRendererFactory();
    }


    public void renderDataLeakageWarning(Editor editor, int lineNumber, LeakageType leakageType) {
        DataLeakageWarningRenderer dataLeakageWarningRenderer = dataLeakageWarningRendererFactory.GetRendererForLeakageType(leakageType);
     //   if (!dataLeakageWarningRenderer.warningIsDisplayed()) {
            if (editor != null) {
                int y = editor.visualLineToY(lineNumber);
                int offset = y + editor.getLineHeight() * 2;
                int startOffset = editor.getDocument().getLineStartOffset(lineNumber);
                editor.getInlayModel().addBlockElement(startOffset, true, true, 1, dataLeakageWarningRenderer);
                // editor.getInlayModel().addAfterLineEndElement(offset, false, dataLeakageWarningRenderer);

      //      }
        }

    }

    public void clearAllDataLeakageWarnings(Editor editor) {
        disposeInlays(editor);

    }


    private static void disposeInlays(Editor editor) {
        var inlayModel = editor.getInlayModel();
        for (var i : inlayModel.getBlockElementsInRange(0, editor.getDocument().getTextLength())) {
            i.dispose();

        }
    }
}





