package com.github.cd721.data_leakage_plugin.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.NotNull;

public class IPYNBAnalysis extends AnAction {
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Using the event, evaluate the context,
        // and enable or disable the action.

        // only should be available when an IPYNB file is selected

        VirtualFile vFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        String fileName = vFile != null ? vFile.getName() : null;
        if (fileName == null) {
            event.getPresentation().setEnabledAndVisible(false);
            return;
        }
        String[] fileNameArr = fileName.split("\\.");
        String fileExtension = fileNameArr[fileNameArr.length - 1];
        if (fileExtension.equals("ipynb")) {
            event.getPresentation().setEnabledAndVisible(true);
            return;
        }

        event.getPresentation().setEnabledAndVisible(false);
        return;

//            if (fileName) {}
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // convert IPYNB file to python and perform analysis on it,
        // naming the functions that have leakage
        Project currentProject = event.getProject();
        StringBuilder message =
                new StringBuilder(event.getPresentation().getText() + " Selected!");
        // If an element is selected in the editor, add info about it.
        Navigatable selectedElement = event.getData(CommonDataKeys.NAVIGATABLE);
        if (selectedElement != null) {
            message.append("\nRunning leakage analysis on: ").append(selectedElement);
        }
        String title = event.getPresentation().getDescription();
        Messages.showMessageDialog(
                currentProject,
                message.toString(),
                title,
                Messages.getInformationIcon());
    }
}
