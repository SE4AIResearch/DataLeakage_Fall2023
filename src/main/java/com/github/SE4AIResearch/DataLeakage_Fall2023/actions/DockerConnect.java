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
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
//        DockerClient dockerClient;
//
        Project currentProject = event.getProject();
        StringBuilder message = new StringBuilder();
//
//        try {
//            DockerClientConfig standard = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
//            DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
//                    .dockerHost(standard.getDockerHost())
//                    .sslConfig(standard.getSSLConfig())
//                    .maxConnections(100)
//                    .connectionTimeout(Duration.ofSeconds(30))
//                    .responseTimeout(Duration.ofSeconds(45))
//                    .build();
//            dockerClient = DockerClientImpl.getInstance(standard, httpClient);
//            List<Container> containers = dockerClient.listContainersCmd().exec();
//            for (Container c : containers) {
//                message.append(c.toString());
//            }
//        } catch(Error e) {
//            message.append(e.toString());
//        }

        String projectPath = currentProject.getBasePath();
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        String filePath = psiFile.getVirtualFile().getPath();

        ConnectClient connectClient = new ConnectClient();
        FileChanger fileChanger = new FileChanger(projectPath);
        try {
            String initOut = fileChanger.inititalizeTempDir();
            message.append(initOut);
            String copyOut = fileChanger.copyToTempDir(filePath);
            message.append(copyOut);
            File workingDir = fileChanger.getWorkingDirectory();

            if (!connectClient.checkImageOnMachine()) {
                connectClient.pullImage();
            }

            connectClient.runLeakageAnalysis(workingDir, copyOut);

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
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
