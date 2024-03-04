package com.github.SE4AIResearch.DataLeakage_Fall2023.actions;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.parsers.LeakageAnalysisParser;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.PythonLanguage;
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
