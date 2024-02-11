package com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api;

// Import DockerJavaAPI library
import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

// Import java libraries
import java.util.ArrayList;
import java.util.Arrays;
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
     * @return boolean true if the image is on the machine false otherwise
     */
    public boolean checkImageOnMachine() {
        List<Image> images = dockerClient.listImagesCmd().exec();
        for(Image i : images) {
            if(i.getRepoTags()[0].equals("bkreiser01/leakage-analysis:latest"))
                return true;
        }
        return false;
    }

    /**
     * Pulls the bkreiser/leakage-analysis image from dockerhub
     * @return true if pullled, false if not
     */
    public boolean pullImage() throws InterruptedException {
        List<SearchItem> items = dockerClient.searchImagesCmd("bkreiser01/leakage-analysis").exec();
        return dockerClient.pullImageCmd("bkreiser01/leakage-analysis")
                    .withTag("latest")
                    .exec(new PullImageResultCallback())
                    .awaitCompletion(180, TimeUnit.SECONDS);
    }

    /**
     * This function creates and run the LAT docker container
     * @param filePath - The path to the file to run the LAT on
     * @param fileName - The name of the file to run the LAT on
     * @return String containing the container ID
     */
    public String runLeakageAnalysis(File filePath, String fileName) {
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

        containers.add(containerId);

        // Return the ID of the newly created container
        return containerId;
    }

    public void close() {
        containers.forEach((id) -> dockerClient.killContainerCmd(id).exec());
        containers.forEach((id) -> dockerClient.removeContainerCmd(id).exec());
    }

    public boolean checkThenPull () throws InterruptedException {
        if(!this.checkImageOnMachine()){
            this.pullImage();
        }
        return true;
    }
}
