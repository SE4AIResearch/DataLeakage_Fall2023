package com.github.SE4AIResearch.DataLeakage_Fall2023.actions;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.ConnectClient;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.FileChanger;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.intellij.openapi.actionSystem.IdeActions.ACTION_INSPECT_CODE;

public class RunLeakageAnalysis extends AnAction {

    private ConnectClient connectClient = new ConnectClient();
    private FileChanger fileChanger = new FileChanger();

    private static Project getProjectForFile(VirtualFile file) {
        Project project = null;
        if (file != null) {
            project = ProjectLocator.getInstance().guessProjectForFile(file);
        }
        return project;
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project currentProject = null;
        VirtualFile file = null;
//        try {
//             file = event.getData(LangDataKeys.EDITOR).getVirtualFile();
//        } catch (NullPointerException e) {
//            Document currentDoc = FileEditorManager.getInstance(event.getData(LangDataKeys.EDITOR).getProject()).getSelectedTextEditor().getDocument();
//             file = FileDocumentManager.getInstance().getFile(currentDoc);
//
//        }

        try {
            currentProject = event.getData(LangDataKeys.EDITOR).getProject();
            file = event.getData(LangDataKeys.EDITOR).getVirtualFile();
        } catch (NullPointerException e) {
            Messages.showMessageDialog(

                    "Please wait until the Python file is fully loaded before checking for data leakage.",
                    "",
                    Messages.getInformationIcon());
        }


        FileType fileType = null;

        if (file != null) {
            fileType = file.getFileType();
        }
        if (fileType != null && fileType.getName().equals("Python")) { // check that the saved file is a python file


            try {
                connectClient.checkThenPull();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (UnsatisfiedLinkError | NoClassDefFoundError e){
                Messages.showErrorDialog(
                        getProjectForFile(file),
                        "Please start the Docker Engine before running leakage analysis.",
                        ""
                    );
                return;
            }

            File tempDirectory;
            String fileName;
            try {
                if (fileChanger.getTempDirectory() == null) {
                    fileChanger.initializeTempDir();
                } else {
                    fileChanger.clearTempDir();
                }
                tempDirectory = fileChanger.getTempDirectory();
                fileName = fileChanger.copyToTempDir(file.getPath());
                String factFolderPath = tempDirectory.toPath().resolve(file.getNameWithoutExtension() + "-fact").toString();
                LeakageOutput.setFactFolderPath(Paths.get(tempDirectory.getCanonicalPath(), file.getNameWithoutExtension()) + "-fact");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (tempDirectory != null && fileName != null) {
                try {
                    connectClient.runLeakageAnalysis(tempDirectory, fileName, event);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        Messages.showMessageDialog(
                getProjectForFile(file),
                "Leakage Analysis Complete.",
                "",
                Messages.getInformationIcon());


//        Project currentProject = event.getProject();
//        StringBuilder message = new StringBuilder();
//
//        String projectPath = currentProject.getBasePath();
//        final PsiFile psiFile =
//                PsiDocumentManager.getInstance(currentProject).getPsiFile(   event.getData(PlatformDataKeys.EDITOR).getDocument());
////
////        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
//        String filePath = psiFile.getVirtualFile().getPath();
//
//        ConnectClient connectClient = new ConnectClient();
//        FileChanger fileChanger = new FileChanger();
//        try {
//            String initOut = fileChanger.initializeTempDir();
//            message.append(initOut);
//            String copyOut = fileChanger.copyToTempDir(filePath);
//            message.append(copyOut);
//            File workingDir = fileChanger.getWorkingDirectory();
//
//            if (!connectClient.checkImageOnMachine()) {
//                connectClient.pullImage();
//            }
//
//            connectClient.runLeakageAnalysis(workingDir, copyOut);
        //connectClient.close();

//            boolean isDeleted = fileChanger.deleteTempDir();
//            message.append(isDeleted);
//            message.append("Before:");
//            message.append(connectClient.checkImageOnMachine());
//            message.append("\nPulling:");
//            message.append(connectClient.pullImage());
//            message.append("\nAfter:");
//            message.append(connectClient.checkImageOnMachine());
//            message.append(connectClient.listImages());
//            connectClient.runLeakageAnalysis(filePath);
//        } catch(Error e) {
//            message.append(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//        Messages.showMessageDialog(
//                currentProject,
//                message.toString(),
//                "",
//                Messages.getInformationIcon());
    }
}
