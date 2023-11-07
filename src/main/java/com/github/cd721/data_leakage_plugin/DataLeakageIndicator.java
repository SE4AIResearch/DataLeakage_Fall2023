package com.github.cd721.data_leakage_plugin;

import com.intellij.openapi.editor.Editor;

public class DataLeakageIndicator  {
    private final DataLeakageWarningRenderer dataLeakageWarningRenderer;

    public DataLeakageIndicator() {

        this.dataLeakageWarningRenderer = new DataLeakageWarningRenderer();

    }



    public void renderDataLeakageWarning(Editor editor) {
        if (!dataLeakageWarningRenderer.isBlockInlayDisplayed()) {
            if (editor != null) {
                addBlockElement(editor);
            }
        } else {
            disposeInlays(editor);
        }

    }


    private void addBlockElement(Editor editor) {
        editor.getInlayModel().addBlockElement(0, true, true, 1, dataLeakageWarningRenderer);
    }

    private static void disposeInlays(Editor editor) {
        for (var i : editor.getInlayModel().getBlockElementsInRange(0, editor.getDocument().getTextLength())) {
            i.dispose();
        }
    }

}

