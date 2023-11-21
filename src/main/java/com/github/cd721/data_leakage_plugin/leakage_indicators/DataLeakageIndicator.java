package com.github.cd721.data_leakage_plugin.leakage_indicators;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.listeners.ClickListener;
import com.github.cd721.data_leakage_plugin.warning_renderers.DataLeakageWarningRenderer;
import com.github.cd721.data_leakage_plugin.warning_renderers.DataLeakageWarningRendererFactory;
import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.InlayParameterHintsProvider;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public class DataLeakageIndicator extends InlayHintsCollector {
    private final DataLeakageWarningRendererFactory dataLeakageWarningRendererFactory;

    public DataLeakageIndicator(@NotNull Editor editor) {
        super(editor);


        dataLeakageWarningRendererFactory = new DataLeakageWarningRendererFactory();
    }


    public void renderDataLeakageWarning(Editor editor, int lineNumber, LeakageType leakageType) {
        DataLeakageWarningRenderer dataLeakageWarningRenderer = dataLeakageWarningRendererFactory.GetRendererForLeakageType(leakageType);
        if (!dataLeakageWarningRenderer.warningIsDisplayed()) {
            if (editor != null) {
<<<<<<< HEAD
<<<<<<< Updated upstream
              //  int y = editor.visualLineToY(lineNumber);

               // int offset = y+editor.getLineHeight()*2;
              //  int visualLineComputed = editor.offsetToVisualLine(offset,true);
=======
                int y = editor.visualLineToY(lineNumber);
                int offset = y+editor.getLineHeight()*3;
>>>>>>> parent of 14f3653 (move inlay one line up)
              //  editor.getInlayModel().addListener(new ClickListener(),);
               int startOffset = editor.getDocument().getLineStartOffset(lineNumber);
               int endOffset = editor.getDocument().getLineEndOffset(lineNumber);




                editor.getInlayModel().addBlockElement(startOffset, false, true, 1, dataLeakageWarningRenderer);
               // editor.getInlayModel().addAfterLineEndElement(offset, false, dataLeakageWarningRenderer);

=======
                int y = editor.visualLineToY(lineNumber);
                int offset = y + editor.getLineHeight();
                //   collect()
>>>>>>> Stashed changes
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
        if (psiElement instanceof PsiComment) {
            inlayHintsSink.addBlockElement(editor.getAscent(), false, true, 1, getFactory().smallText("Overlap Leakage"));

        }
        return true;
    }
}


