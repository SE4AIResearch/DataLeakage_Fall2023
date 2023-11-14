package com.github.cd721.data_leakage_plugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class DataLeakageIndicator implements Inlay<DataLeakageWarningRenderer> {
    private final DataLeakageWarningRenderer dataLeakageWarningRenderer;

    public DataLeakageIndicator() {

        this.dataLeakageWarningRenderer = new DataLeakageWarningRenderer();
    }


    public void renderDataLeakageWarning(Editor editor, int lineNumber) {
        if (!dataLeakageWarningRenderer.isBlockInlayDisplayed()) {
            if (editor != null) {
                addBlockElement(editor, lineNumber);
            }
        }

    }

    public void clearDataLeakageWarnings(Editor editor){
        disposeInlays(editor);
    }


    private void addBlockElement(Editor editor, int lineNumber) {
        int offset = editor.visualLineToY(lineNumber);
        editor.getInlayModel().addBlockElement(offset, false, true, 1, dataLeakageWarningRenderer);
    }

    private static void disposeInlays(Editor editor) {
        for (var i : editor.getInlayModel().getBlockElementsInRange(0, editor.getDocument().getTextLength())) {
            i.dispose();
        }
    }

    @Override
    public @NotNull Editor getEditor() {
        return null;
    }

    @Override
    public @NotNull Placement getPlacement() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public boolean isRelatedToPrecedingText() {
        return false;
    }

    @Override
    public @NotNull VisualPosition getVisualPosition() {
        return null;
    }

    @Override
    public @Nullable Rectangle getBounds() {
        return null;
    }

    @Override
    public @NotNull DataLeakageWarningRenderer getRenderer() {
        return null;
    }

    @Override
    public int getWidthInPixels() {
        return 0;
    }

    @Override
    public int getHeightInPixels() {
        return 0;
    }

    @Override
    public @Nullable GutterIconRenderer getGutterIconRenderer() {
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void repaint() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public <T> @NotNull T putUserDataIfAbsent(@NotNull Key<T> key, @NotNull T value) {
        return null;
    }

    @Override
    public <T> boolean replace(@NotNull Key<T> key, @Nullable T oldValue, @Nullable T newValue) {
        return false;
    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {

    }
}

