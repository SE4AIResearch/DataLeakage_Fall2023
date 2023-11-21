package com.github.cd721.data_leakage_plugin.leakage_indicators;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.listeners.ClickListener;
import com.github.cd721.data_leakage_plugin.warning_renderers.DataLeakageWarningRenderer;
import com.github.cd721.data_leakage_plugin.warning_renderers.DataLeakageWarningRendererFactory;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class DataLeakageIndicator implements InlayHintsCollector {
    private final DataLeakageWarningRendererFactory dataLeakageWarningRendererFactory;

    public DataLeakageIndicator() {
        dataLeakageWarningRendererFactory = new DataLeakageWarningRendererFactory();
    }


    public void renderDataLeakageWarning(Editor editor, int lineNumber, LeakageType leakageType) {
        DataLeakageWarningRenderer dataLeakageWarningRenderer = dataLeakageWarningRendererFactory.GetRendererForLeakageType(leakageType);
        if (!dataLeakageWarningRenderer.warningIsDisplayed()) {
            if (editor != null) {
                int y = editor.visualLineToY(lineNumber);
                int offset = y+editor.getLineHeight()*2;
              //  editor.getInlayModel().addListener(new ClickListener(),);
                int startOffset = editor.getDocument().getLineStartOffset(lineNumber);



                editor.getInlayModel().addBlockElement(startOffset, true, true, 1, dataLeakageWarningRenderer);

               // editor.getInlayModel().addAfterLineEndElement(offset, false, dataLeakageWarningRenderer);

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


    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        return false;
    }
}


