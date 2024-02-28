package com.github.SE4AIResearch.DataLeakage_Fall2023.actions;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.ConnectClient;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.FileChanger;
import com.github.SE4AIResearch.DataLeakage_Fall2023.notifiers.LeakageNotifier;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
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
      } catch (NullPointerException e) {
//            LeakageNotifier.notifyNotLoaded("Please wait until the project is fully loaded before checking for data leakage");
         Messages.showMessageDialog(
               "Please wait until the Python file is fully loaded before checking for data leakage.",
               "",
               Messages.getInformationIcon());
      }

      try {
         file = event.getData(LangDataKeys.EDITOR).getVirtualFile();
      } catch (NullPointerException e) {
         LeakageNotifier.notifyError(currentProject, "Must open a python file to run leakage analysis");
      }

      FileType fileType = null;

      if (file != null) {
         fileType = file.getFileType();
      }

      if (fileType != null && fileType.getName().equals("Python")) { // check that the file is a python file
         VirtualFile finalFile = file;
         Runnable runLeakage = new Runnable() {
            public void run() {
               ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();

               try {
                  if (!connectClient.checkImageOnMachine()) {
                     try {
                        indicator.setText("Pulling the leakage-analysis docker image");
                        indicator.setText2("Pulling");
                        connectClient.pullImage();
                        indicator.setFraction(0.5);
                     } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                     }
                  }
               } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
                  Messages.showErrorDialog(
                        getProjectForFile(file),
                        "Please start the Docker Engine before running leakage analysis.",
                        ""
                  );
                  return;
               }

//               if (indicator.isCanceled()) { throw new ProcessCanceledException(); }

               File tempDirectory;
               String fileName;

               try {
                  if (fileChanger.getTempDirectory() == null) {
                     indicator.setText("Creating temporary directory");
                     fileChanger.initializeTempDir();
                  } else {
                     indicator.setText("Cleaning up temporary directory");
                     fileChanger.clearTempDir();
                  }
                  indicator.setFraction(indicator.getFraction() + 0.1);

                  tempDirectory = fileChanger.getTempDirectory();

                  indicator.setText("Copying " + finalFile.getName() + " to temporary directory");
                  fileName = fileChanger.copyToTempDir(finalFile.getPath());
                  indicator.setFraction(indicator.getFraction() + 0.1);

                  String factFolderPath = tempDirectory.toPath().resolve(finalFile.getNameWithoutExtension() + "-fact").toString();
                  LeakageOutput.setFactFolderPath(Paths.get(tempDirectory.getCanonicalPath(), finalFile.getNameWithoutExtension()) + "-fact");
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }

               try {
                  indicator.setText("Running analysis");
                  indicator.setText2("Running");
                  connectClient.runLeakageAnalysis(tempDirectory, fileName, event);
                  indicator.setFraction(1);
               } catch (InterruptedException e) {
                  throw new RuntimeException(e);
               }

               indicator.stop();
            }
         };

         ProgressManager.getInstance().runProcessWithProgressSynchronously(
               runLeakage,
               "Running Data Leakage Analysis",
               false,
               currentProject
         );

      }

      LeakageNotifier.notifyInformation(currentProject, "Leakage Analysis Complete.");
   }
}

