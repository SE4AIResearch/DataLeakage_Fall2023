package com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
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

    public int checkImageAvailable() throws InterruptedException {

        return -1;
    }

    public int checkImageOnMachine() {
        // list the images and check that it matches bkreiser
        return -1;
    }

    public int pullImage() throws InterruptedException {
        try {
            dockerClient.pullImageCmd("bkreiser01/leakage-analysis")
                    .withTag("git")
                    .exec(new PullImageResultCallback())
                    .awaitCompletion(30, TimeUnit.SECONDS);
        } catch (Error e) {
            // TODO Probably want to log this error
            return -1;
        }
        return 1;
    }

    //Input should be file location
    public int runLeakageAnalysis() {

        return -1;
    }

    /**
     * Function that uses the docker image to run things
     */
    public void performAnalysis() {
        // Want it to return the root location of the output probably
    }

}
