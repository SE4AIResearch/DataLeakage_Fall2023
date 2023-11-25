package com.github.cd721.data_leakage_plugin.warning_renderers;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static javax.swing.UIManager.getFont;

public abstract class DataLeakageWarningRenderer implements EditorCustomElementRenderer {

    protected abstract String getMessage();


    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
        Editor editor = inlay.getEditor();
        g.setColor(JBColor.GRAY);
        g.setFont(getFont(editor));

        int lineNumber = inlay.getEditor().getDocument().getLineNumber(inlay.getOffset());

        g.drawString(getMessage(), targetRegion.x, (inlay.getOffset()));
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        return 50;
    }


}
