package com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api;

import com.github.dockerjava.api.DockerClient;
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

import java.util.List;
import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;


/**
 * This class is mean to establish a connection between the docker container and the plugin
 */
public class ConnectClient {
    private static DockerClient dockerClient;

    public ConnectClient() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        dockerClient = DockerClientBuilder.getInstance()
                .withDockerHttpClient(httpClient).build();
    }

    public String listContainers() {
        List<Container> containers = dockerClient.listContainersCmd().exec();
        StringBuilder containersString = new StringBuilder();
        for (Container c : containers) {
            containersString.append(c.toString());
        }
        return containersString.toString();
    }

    public String listImages() {
        List<Image> images = dockerClient.listImagesCmd().exec();
        StringBuilder imagesSB = new StringBuilder();
        for(Image i : images) {
            imagesSB.append(i.toString());
        }
        return imagesSB.toString();
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

    public boolean pullImage() throws InterruptedException {
        List<SearchItem> items = dockerClient.searchImagesCmd("bkreiser01/leakage-analysis").exec();
        return dockerClient.pullImageCmd("bkreiser01/leakage-analysis")
                    .withTag("latest")
                    .exec(new PullImageResultCallback())
                    .awaitCompletion(180, TimeUnit.SECONDS);
    }

    public boolean runLeakage() {
        return false;
    }

    public boolean createVolume(String projectPath) {
        //Should probably return the volume path?
        return false;
    }

    //Input should be file location
    public String runLeakageAnalysis(File filePath, String fileName) {
        // Get the path to the file on the users machine
        String path2file = filePath.toString();

        // Create the container
        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd("leakage")
                .withImage("bkreiser01/leakage-analysis")
                .withBinds(Bind.parse(path2file+":/execute"))
                .withCmd("/execute/"+fileName).exec();

        // Get the container's id
        String containerId = createContainerResponse.getId();

        // InspectContainerResponse container = dockerClient.inspectContainerCmd(containerId).exec();
        dockerClient.startContainerCmd(containerId).exec();

        return containerId;
    }

    public boolean x () throws InterruptedException {
        if(!this.checkImageOnMachine()){
            this.pullImage();
        }
        return true;
    }

    /**
     * Function that uses the docker image to run things
     */
    public void performAnalysis() {
        // Want it to return the root location of the output probably
    }

}
