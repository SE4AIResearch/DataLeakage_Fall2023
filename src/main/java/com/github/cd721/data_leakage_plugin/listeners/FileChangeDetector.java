package com.github.cd721.data_leakage_plugin.listeners;

import com.github.cd721.data_leakage_plugin.BinaryApi;
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

public class FileChangeDetector implements BulkFileListener {


    public FileChangeDetector() {

    }

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent event : events) {
            //TODO: run leakage analysis

            BinaryApi api = new BinaryApi("C:/dev/paper-sample-4.py");
            api.analysis();

        }

    }

    private static boolean theChangedFileIsInTheCurrentProject(VirtualFile file) {
        var project = getProjectForFile(file);
        return project != null && ProjectFileIndex.getInstance(project).isInProject(file);
    }

    private boolean theChangedFileIsCurrentlyBeingEdited(VFileEvent event) {
        var editor = getEditorForFileChanged(event);
        if (editor == null) {
            return false;
        }
        var fileBeingEdited = editor.getVirtualFile();

        return fileBeingEdited.equals(event.getFile());
    }


    private boolean aPythonFileWasChanged(VFileEvent event) {
        return event.getPath().endsWith(".py") && event instanceof VFileContentChangeEvent;
    }


    private static Project getProjectForFile(VirtualFile file) {
        Project project = null;
        if (file != null) {
            project = ProjectLocator.getInstance().guessProjectForFile(file);
        }
        return project;
    }

    public Editor getEditorForFileChanged(VFileEvent event) {
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

        return editor;
    }
}
