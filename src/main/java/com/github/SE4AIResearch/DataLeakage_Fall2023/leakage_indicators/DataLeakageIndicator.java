package com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_indicators;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.warning_renderers.DataLeakageWarningRenderer;
import com.github.SE4AIResearch.DataLeakage_Fall2023.warning_renderers.DataLeakageWarningRendererFactory;
import com.intellij.openapi.editor.Editor;


public class DataLeakageIndicator {
    private final DataLeakageWarningRendererFactory dataLeakageWarningRendererFactory;

    public DataLeakageIndicator() {
        dataLeakageWarningRendererFactory = new DataLeakageWarningRendererFactory();
    }


    public void renderDataLeakageWarning(Editor editor, int lineNumber, LeakageType leakageType) {
        DataLeakageWarningRenderer dataLeakageWarningRenderer = dataLeakageWarningRendererFactory.GetRendererForLeakageType(leakageType);


        if (editor != null) {
            int y = editor.visualLineToY(lineNumber);
            int offset = y + editor.getLineHeight() * 2;
            int startOffset = editor.getDocument().getLineStartOffset(lineNumber);
            if (!dataLeakageWarningRenderer.warningIsDisplayed(editor, startOffset)) {

                editor.getInlayModel().addBlockElement(startOffset, true, true, 1, dataLeakageWarningRenderer);
                // editor.getInlayModel().addAfterLineEndElement(offset, false, dataLeakageWarningRenderer);

            }
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





