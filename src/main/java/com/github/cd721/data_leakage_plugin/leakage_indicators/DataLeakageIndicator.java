package com.github.cd721.data_leakage_plugin.leakage_indicators;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.warning_renderers.DataLeakageWarningRenderer;
import com.github.cd721.data_leakage_plugin.warning_renderers.DataLeakageWarningRendererFactory;
import com.intellij.openapi.editor.Editor;


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

