package com.github.cd721.data_leakage_plugin.actions;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.parsers.LeakageAnalysisParser;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyAction extends AnAction {
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

//    public PopupDialogAction() {
//        super();
//    }
//
//    public PopupDialogAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
//        super(text, description, icon);
//    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Using the event, evalbouate the context,
        // and enable or disable the action.
        Project project = event.getProject();
//        event.getPresentation().setEnabledAndVisible(project != null);

        VirtualFile vFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        String fileName = vFile != null ? vFile.getName() : null;
        if (fileName == null) {
            event.getPresentation().setEnabledAndVisible(false);
            return;
        }
        String[] fileNameArr = fileName.split("\\.");
        String fileExtension = fileNameArr[fileNameArr.length - 1];
        event.getPresentation().setEnabledAndVisible(fileExtension.equals("py"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Using the event, implement an action.
        // For example, create and show a dialog.


        Project currentProject = event.getProject();
        StringBuilder message = new StringBuilder();
//                new StringBuilder(event.getPresentation().getText() + " Selected!");

        LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();
        leakageAnalysisParser.LeakageLineNumbers();
        if (!leakageAnalysisParser.isLeakageDetected()) {
            message.append("No Leakage Detected");
            return;
        }

        List<LeakageInstance> instances = leakageAnalysisParser.LeakageInstances();
        for (LeakageInstance instance : instances) {
            message.append(instance.type() + " at line " + Integer.toString(instance.lineNumber())  + "\n");
        }

        // If an element is selected in the editor, add info about it.
        Navigatable selectedElement = event.getData(CommonDataKeys.NAVIGATABLE);
        Editor editor = event.getData(LangDataKeys.EDITOR);
        VirtualFile editorFile = null;
        String fileName = "";

        if (editor != null) {
            editorFile = editor.getVirtualFile();
        }

        if (editorFile != null) {
            fileName = editorFile.getName();
        }

        if (selectedElement != null) {
            fileName = selectedElement.toString();
        }

//        String title = event.getPresentation().getDescription();
        Messages.showMessageDialog(
                currentProject,
                message.toString(),
                "Analysis on " + fileName,
                Messages.getInformationIcon());
    }

    // Override getActionUpdateThread() when you target 2022.3 or later!
}
