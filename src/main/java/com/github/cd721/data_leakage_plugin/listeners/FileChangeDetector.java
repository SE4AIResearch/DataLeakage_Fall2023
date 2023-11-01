package com.github.cd721.data_leakage_plugin.listeners;

import com.github.cd721.data_leakage_plugin.OverlapLeakageDetector;
import com.intellij.codeInsight.codeVision.CodeVisionAnchorKind;
import com.intellij.codeInsight.codeVision.CodeVisionProvider;
import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering;
import com.intellij.codeInsight.codeVision.CodeVisionState;
import com.intellij.codeInsight.codeVision.ui.model.TextCodeVisionEntry;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.UIManager.getFont;

public class FileChangeDetector implements BulkFileListener, CodeVisionProvider<Object> {
    private final OverlapLeakageDetector overlapleakagedetected;
    private boolean blockInlayDisplayed;

    public FileChangeDetector() {
        this.overlapleakagedetected = new OverlapLeakageDetector();

        blockInlayDisplayed = false;
    }

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent event : events) {
            var file = event.getFile();
            Project project = null;
            if (file != null) {
                project = ProjectLocator.getInstance().guessProjectForFile(file);
            }
            TextEditor currentEditor = null;
            if (project != null) {
                currentEditor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor();
            }
            Editor editor = null;
            if (currentEditor != null) {
                editor = currentEditor.getEditor();
            }
            if (!blockInlayDisplayed) {

                if (project != null && ProjectFileIndex.getInstance(project).isInProject(file)) {

                    if (event.getPath().endsWith(".py") && event instanceof VFileContentChangeEvent) {
                        if (overlapleakagedetected.OverlapLeakageDetected()) {

                            if (editor != null) {
                                editor.getInlayModel().addBlockElement(0, true, true, 1,
                                        new EditorCustomElementRenderer() {
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
                                        })
                                ;
                            }
                        }
                    }
                }
            }
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


    @Override
    public Object precomputeOnUiThread(@NotNull Editor editor) {
        return null;
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
    public List<CodeVisionRelativeOrdering> getRelativeOrderings() {
        return null;
    }
}