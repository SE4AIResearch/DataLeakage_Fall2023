package com.github.cd721.data_leakage_plugin.listeners;

import com.github.cd721.data_leakage_plugin.DataLeakageIndicator;
import com.github.cd721.data_leakage_plugin.LeakageAnalysisParser;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.apache.tools.ant.types.resources.MultiRootFileSet.SetType.file;

public class FileChangeDetector implements BulkFileListener {
    private boolean fileChanged;
    private Editor editorForFileChanged;
    private final LeakageAnalysisParser leakageAnalysisParser;

    private final DataLeakageIndicator dataLeakageIndicator;

    public FileChangeDetector() {

        fileChanged = false;
        leakageAnalysisParser = new LeakageAnalysisParser();
        dataLeakageIndicator = new DataLeakageIndicator();
    }

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent event : events) {


            setEditorForFileChanged(event);

            if (theChangedFileIsInTheCurrentProject(event.getFile())) {

                if (aPythonFileWasChanged(event)) {


                    fileChanged = true;
                    if (leakageAnalysisParser.isOverlapLeakageDetected()) {
                        dataLeakageIndicator.renderDataLeakageWarning(getEditorForFileChanged());
                    }
                }
            }
        }


    }

    private static boolean theChangedFileIsInTheCurrentProject(VirtualFile file) {
        var project = getProjectForFile(file);
        return project != null && ProjectFileIndex.getInstance(project).isInProject(file);
    }


    private boolean aPythonFileWasChanged(VFileEvent event) {
        return event.getPath().endsWith(".py") && event instanceof VFileContentChangeEvent;
    }

    public boolean isFileChanged() {
        return fileChanged;
    }

    public Editor getEditorForFileChanged() {

        return editorForFileChanged;

    }

    private static Project getProjectForFile(VirtualFile file) {
        Project project = null;
        if (file != null) {
            project = ProjectLocator.getInstance().guessProjectForFile(file);
        }
        return project;
    }

    public void setEditorForFileChanged(VFileEvent event) {
        var file = event.getFile();
        var project = getProjectForFile(file);
        TextEditor currentEditor = null;
        if (project != null) {
            currentEditor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor();
        }
        Editor editor = null;
        if (currentEditor != null) {
            editor = currentEditor.getEditor();
        }

        this.editorForFileChanged = editor;
    }
}
