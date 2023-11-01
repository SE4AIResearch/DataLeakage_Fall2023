package com.github.cd721.data_leakage_plugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static javax.swing.UIManager.getFont;

public class DataLeakageWarningRenderer implements EditorCustomElementRenderer {
    private boolean blockInlayDisplayed;

    @Override
        public int calcWidthInPixels(@NotNull Inlay inlay) {
            return 30;
        }

        @Override
        public int calcHeightInPixels(@NotNull Inlay inlay) {
            return 15;
        }

        @Override
        public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
            Editor editor = inlay.getEditor();
            g.setColor(JBColor.GRAY);
            g.setFont(getFont(editor));
            g.drawString("Your code may contain data leakage.", targetRegion.x, targetRegion.y);
            blockInlayDisplayed = true;
        }


    public boolean isBlockInlayDisplayed() {
        return blockInlayDisplayed;
    }
}
