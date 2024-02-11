package com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api;

// Import DockerJavaAPI library

import com.github.dockerjava.api.*;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.FileDocumentManager;

// Import java libraries
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;


/**
 * This class creates a Docker client to connect to the hosts docker daemon
 */
public class ConnectClient {

    private static DockerClient dockerClient;
    private ArrayList<String> containers; // Arraylist of all containers

    /**
     * Constructor for ConnectClient object
     * https://github.com/docker-java/docker-java/blob/main/docs/getting_started.md
     */
    public ConnectClient() {
        // Create a default client config
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        // Create a default http client
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        // Create the docker client builder
        dockerClient = DockerClientBuilder.getInstance()
                .withDockerHttpClient(httpClient).build();

        // Create empty arraylist
        containers = new ArrayList<String>();
    }

    /**
     * Checks if the bkreiser/leakage-analysis image is available in docker
     *
     * @return boolean true if the image is on the machine false otherwise
     */
    public boolean checkImageOnMachine() {
        List<Image> images = dockerClient.listImagesCmd().exec();
        for (Image i : images) {
            if (i.getRepoTags()[0].equals("bkreiser01/leakage-analysis:latest"))
                return true;
        }
        return false;
    }

    /**
     * Pulls the bkreiser/leakage-analysis image from dockerhub
     *
     * @return true if pullled, false if not
     */
    public boolean pullImage() throws InterruptedException {
        List<SearchItem> items = dockerClient.searchImagesCmd("bkreiser01/leakage-analysis").exec();
        return dockerClient.pullImageCmd("bkreiser01/leakage-analysis")
                .withTag("latest")
                .exec(new PullImageResultCallback())
                .awaitCompletion(600, TimeUnit.SECONDS);
    }

    /**
     * This function creates and run the LAT docker container
     *
     * @param filePath - The path to the file to run the LAT on
     * @param fileName - The name of the file to run the LAT on
     * @return String containing the container ID
     */
    public Boolean runLeakageAnalysis(File filePath, String fileName, AnActionEvent event) throws InterruptedException {
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
        close(containerId);

        // Save file
        FileDocumentManager.getInstance().reloadFiles((event.getData(LangDataKeys.EDITOR)).getVirtualFile());

        // Return the ID of the newly created container
        return true;
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

    public boolean checkThenPull() throws InterruptedException {
        if (!this.checkImageOnMachine()) {
            this.pullImage();
        }
        return true;
    }

    private static ResultCallback<WaitResponse> save(AnActionEvent event, String containerId) {

        return new ResultCallback<WaitResponse>() {
            @Override
            public void onStart(Closeable closeable) {
                dockerClient.startContainerCmd(containerId).exec();
            }

            @Override
            public void onNext(WaitResponse object) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                FileDocumentManager.getInstance().saveDocument((event.getData(LangDataKeys.EDITOR)).getDocument());
                FileDocumentManager.getInstance().reloadFiles((event.getData(LangDataKeys.EDITOR)).getVirtualFile());
         //       FileDocumentManager.getInstance().reloadFromDisk((event.getData(LangDataKeys.EDITOR)).getDocument());
            }

            @Override
            public void close() throws IOException {

                FileDocumentManager.getInstance().saveDocument((event.getData(LangDataKeys.EDITOR)).getDocument());
                FileDocumentManager.getInstance().reloadFiles((event.getData(LangDataKeys.EDITOR)).getVirtualFile());
       //         FileDocumentManager.getInstance().reloadFromDisk((event.getData(LangDataKeys.EDITOR)).getDocument());

            }
        };
    }
}
