package com.github.cd721.data_leakage_plugin;

import com.intellij.codeInsight.codeVision.CodeVisionAnchorKind;
import com.intellij.codeInsight.codeVision.CodeVisionProvider;
import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering;
import com.intellij.codeInsight.codeVision.CodeVisionState;
import com.intellij.codeInsight.codeVision.ui.model.TextCodeVisionEntry;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DataLeakageIndicator implements CodeVisionProvider<Object> {
    private final DataLeakageWarningRenderer dataLeakageWarningRenderer;


    public DataLeakageIndicator() {
        this.dataLeakageWarningRenderer = new DataLeakageWarningRenderer();
    }

    @NotNull
    private CodeVisionState.Ready getReady(String myStr, PsiElement element) {
        var entry = (new TextCodeVisionEntry(myStr, getId(), null, myStr, myStr, new ArrayList<>()));
        List<kotlin.Pair<TextRange, TextCodeVisionEntry>> lenses = new ArrayList<>();
        TextRange textRange = null;
        if (element != null) {
            textRange = (element.getTextRange());
        }
        kotlin.Pair<TextRange, TextCodeVisionEntry> pair = new kotlin.Pair<>(textRange, entry);
        lenses.add(pair);
        CodeVisionState.Ready ready;
        ready = new CodeVisionState.Ready(lenses);
        return ready;
    }

    @NotNull
    @Override
    public CodeVisionState computeCodeVision(@NotNull Editor editor, Object wrapper) {
        String myStr = "There might be overlap leakage in " + editor.getVirtualFile().getName();
        Project project = editor.getProject();
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = null;
        if (project != null) {
            if (file != null) {
                element = PsiManager.getInstance(project).findFile(file).findElementAt(offset);
            }
        }

        return getReady(myStr, element);
    }


    public void renderDataLeakageWarning( Editor editor) {
        if (!dataLeakageWarningRenderer.isBlockInlayDisplayed()) {

                    if (editor != null) {
                        addBlockElement(editor);

                    }
                } else {


                    disposeInlays(editor);
                    // }
                }

    }


    private void addBlockElement(Editor editor) {
        editor.getInlayModel().addBlockElement(0, true, true, 1,
                dataLeakageWarningRenderer);
    }

    private static void disposeInlays(Editor editor) {
        for (var i : editor.getInlayModel().getBlockElementsInRange(0, editor.getDocument().getTextLength())) {
            i.dispose();
        }
    }

    @NotNull
    @Override
    public CodeVisionAnchorKind getDefaultAnchor() {
        return null;
    }

    @NotNull
    @Override
    public String getId() {
        return null;
    }

    @Nls
    @NotNull
    @Override
    public String getName() {
        return null;
    }

    @NotNull
    @Override
    public List<CodeVisionRelativeOrdering> getRelativeOrderings() {
        return null;
    }

    @Override
    public Object precomputeOnUiThread(@NotNull Editor editor) {
        return null;
    }
}

