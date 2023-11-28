package com.github.cd721.data_leakage_plugin.actions;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.parsers.LeakageAnalysisParser;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.PythonLanguage;
import com.jetbrains.python.sdk.PythonEnvUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeakageAnalysisAction extends AnAction {
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        assert psiFile != null;
        event.getPresentation().setEnabledAndVisible(psiFile.getLanguage().isKindOf(PythonLanguage.INSTANCE));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Using the event, implement an action.
        // For example, create and show a dialog.

        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        assert psiFile != null;
        String filePath = psiFile.getVirtualFile().getPath();
        String fileName = psiFile.getVirtualFile().getName();

        Project currentProject = event.getProject();
        StringBuilder message = new StringBuilder();

        LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();
        leakageAnalysisParser.LeakageLineNumbers();
        if (!leakageAnalysisParser.isLeakageDetected()) {
            message.append("No Leakage Detected");
            return;
        }

        List<LeakageInstance> instances = leakageAnalysisParser.LeakageInstances();
        for (LeakageInstance instance : instances) {
            message.append(instance.type() + " at line " + Integer.toString(instance.lineNumber()) + "\n");
        }

        Messages.showMessageDialog(
                currentProject,
                message.toString(),
                "Analysis on " + fileName,
                Messages.getInformationIcon());
    }

    // Override getActionUpdateThread() when you target 2022.3 or later!
}
