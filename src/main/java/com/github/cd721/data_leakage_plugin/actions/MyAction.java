package com.github.cd721.data_leakage_plugin.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.NotNull;

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
        if (fileExtension.equals("py")) {
            event.getPresentation().setEnabledAndVisible(true);
            return;
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Using the event, implement an action.
        // For example, create and show a dialog.
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

    // Override getActionUpdateThread() when you target 2022.3 or later!
}
