package com.github.cd721.data_leakage_plugin.warning_renderers;

import com.github.cd721.data_leakage_plugin.warning_renderers.DataLeakageWarningRenderer;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static javax.swing.UIManager.getFont;

public class OverlapLeakageWarningRenderer extends DataLeakageWarningRenderer {


    private boolean blockInlayDisplayed;


    @Override
    public boolean warningIsDisplayed() {

        return blockInlayDisplayed;
    }

    @Override
    protected String getMessage() {
        return "Your code may contain overlap leakage.";
    }


    @Override
    protected void setWarningDisplayed(boolean inlayDisplayed) {

        blockInlayDisplayed = inlayDisplayed;
    }
}
