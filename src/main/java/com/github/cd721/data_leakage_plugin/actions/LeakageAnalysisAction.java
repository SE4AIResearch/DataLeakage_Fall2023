package com.github.cd721.data_leakage_plugin.actions;

import com.github.cd721.data_leakage_plugin.api.BinaryApi;
import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.data.LeakageOutput;
import com.github.cd721.data_leakage_plugin.leakage_detectors.LeakageDetector;
import com.github.cd721.data_leakage_plugin.listeners.LeakageFileChangeDetector;
import com.github.cd721.data_leakage_plugin.parsers.LeakageAnalysisParser;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.PythonLanguage;
import com.jetbrains.python.sdk.PythonEnvUtil;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.io.FileUtilRt;

import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class LeakageAnalysisAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(LeakageAnalysisAction.class);
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

        BinaryApi binaryApi = null;
//        FileUtilRt.createTempDirectory();
//        Project p = event.getProject();
//        String path = p.getBasePath();
//        VirtualFile[] roots = ModuleRootManager.getInstance(ModuleManager.getInstance(p).getModules()[0]).getSourceRoots();
//        VirtualFile root = roots[0];


        Project currentProject = event.getProject();
        String projectPath = currentProject.getBasePath();


        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        assert psiFile != null;
        String filePath = psiFile.getVirtualFile().getPath();
        String fileName = psiFile.getVirtualFile().getName();

        try{
            binaryApi = new BinaryApi(filePath, projectPath);
        }catch(Exception e){
            LOG.error(e);
        }

        int exitCode = binaryApi.analysis();

        StringBuilder message = new StringBuilder();

        message.append(exitCode);

//        LeakageAnalysisParser leakageAnalysisParser = new LeakageAnalysisParser();
//
//        if (!leakageAnalysisParser.isLeakageDetected()) {
//            message.append("No Leakage Detected");
//            return;
//        }
//
//
//        List<LeakageInstance> instances = leakageAnalysisParser.LeakageInstances();
//        for (LeakageInstance instance : instances) {
//            message.append(instance.type() + " at line " + Integer.toString(instance.lineNumber()) + "\n");
//        }

        Messages.showMessageDialog(
                currentProject,
                message.toString(),
                "Analysis on " + fileName,
                Messages.getInformationIcon());
    }

    // Override getActionUpdateThread() when you target 2022.3 or later!
}
