package com.github.SE4AIResearch.DataLeakage_Fall2023.actions;

import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.ConnectClient;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.FileChanger;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class DockerConnect extends AnAction {
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
    }

    /**
     * actionPerformed is activated whenever the action button is clicked
     * @param event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project currentProject = event.getProject(); // this retrieves the project from the action event, this contains the information about all the files in the project
        StringBuilder message = new StringBuilder(); // this is just initializing the message displayed after the action is performed

        String projectPath = currentProject.getBasePath(); // Just gets the path of the project something like "pycharmProjects/project1"
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE); // this gets the psifile which is the file currently selected in the project window
        String filePath = psiFile.getVirtualFile().getPath(); // this gets the path from the psifile (currently selected file) this can be null and will error out

        ConnectClient connectClient = new ConnectClient(); //constructing new connect client
        FileChanger fileChanger = new FileChanger(projectPath); //constructing new filechanger
        try {
            String initOut = fileChanger.inititalizeTempDir(); // creates the temporary directory and returns it
            message.append(initOut); //appending the temporary directory name to the message displayed
            String copyOut = fileChanger.copyToTempDir(filePath); // copies the file specified earlier to the temporary directory
            message.append(copyOut); // adding the name of the file to the end of the message
            File workingDir = fileChanger.getWorkingDirectory(); //getting the file of the temporary directory

            if (!connectClient.checkImageOnMachine()) { // if the bkreiser/leakage-analysis image is not on the user's docker we pull the image from docker hub
                connectClient.pullImage(); //pull image from docker hub
            }

            connectClient.runLeakageAnalysis(workingDir, copyOut); // run the leakage-analysis on the file in the temporary directory

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

        } catch(Error e) {
            message.append(e);
        } catch (IOException e) {
            throw new RuntimeException(e); // this catch was automatically generated
        } catch (InterruptedException e) {
            throw new RuntimeException(e); //pulling the docker image can result in an interupt exception this catch was automatically generated
        }

        // This displays the message we constructed earlier and displays it to the user
        Messages.showMessageDialog(
                currentProject,
                message.toString(),
                "",
                Messages.getInformationIcon());
    }
}

//public class DockerConnect extends AnAction {
//
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//
//    }
//}
