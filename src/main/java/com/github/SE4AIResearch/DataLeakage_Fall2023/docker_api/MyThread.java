package com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.FileDocumentManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyThread implements  Runnable{
    private File filePath;
    private String fileName;
    private AnActionEvent event;

    private static DockerClient dockerClient;
    public MyThread(File filePath, String fileName, AnActionEvent event){
        this.filePath=filePath;
        this.fileName = fileName;
        this.event= event;
    }
    @Override
    public void run()  {
        // Get the path to the file on the users machine
        String path2file = filePath.toString();
        List<String> commands = Arrays.asList("/execute/" + fileName, "-o");

        // Create the container
        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd("leakage")
                .withImage("bkreiser01/leakage-analysis")
                .withBinds(Bind.parse(path2file + ":/execute"))
                .withCmd(commands).exec();

        // Get the container's ID
        String containerId = createContainerResponse.getId();

        // Execute the container by ID
        dockerClient.startContainerCmd(containerId).exec();
        try {
            close(containerId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Save file
        FileDocumentManager.getInstance().reloadFiles((event.getData(LangDataKeys.EDITOR)).getVirtualFile());

        // Return the ID of the newly created container
//??
    }


    private ArrayList<String> getRunningContainers() {
        // Get all running docker containers
        List<Container> runningContainers = dockerClient.listContainersCmd().withStatusFilter(Collections.singleton("running")).exec();

        // Store all running containers into an arraylist
        ArrayList<String> runningContainerIds = new ArrayList<String>();
        runningContainers.forEach(container -> runningContainerIds.add(container.getId()));

        return runningContainerIds;
    }

    public void close(String containerId) throws InterruptedException {
        // If the container is running, wait until it stops running
        while (getRunningContainers().contains(containerId)) {
            TimeUnit.SECONDS.sleep(1);
        }

        // Remove the container
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
