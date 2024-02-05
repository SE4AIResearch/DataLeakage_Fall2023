package com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DockerClientBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * This class is mean to establish a connection between the docker container and the plugin
 */
public class ConnectClient {
    private static DockerClient dockerClient;

    public ConnectClient() {
        dockerClient = DockerClientBuilder.getInstance().build();
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
    public int runLeakageAnalysis(String filePath) {
        //should only run leakage on active python file
        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd("leakage")
                .withImage("bkreiser01/leakage-analysis")
                .withBinds(Bind.parse(filePath+":/dev")).exec();
        String containerId = createContainerResponse.getId();
        dockerClient.startContainerCmd(containerId).exec();
        return -1;
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
