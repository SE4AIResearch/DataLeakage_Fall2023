package com.github.cd721.data_leakage_plugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static javax.swing.UIManager.getFont;

public class OverlapLeakageWarningRenderer extends DataLeakageWarningRenderer  {


private boolean blockInlayDisplayed;
    @Override
        public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
            Editor editor = inlay.getEditor();
            g.setColor(JBColor.GRAY);
            g.setFont(getFont(editor));

        int lineNumber = inlay.getEditor().getDocument().getLineNumber(inlay.getOffset());

        g.drawString("Your code may contain overlap leakage.", targetRegion.x, (inlay.getOffset()));

        blockInlayDisplayed = true;
        }


    @Override
    public boolean warningIsDisplayed() {

        return blockInlayDisplayed;
    }
}
