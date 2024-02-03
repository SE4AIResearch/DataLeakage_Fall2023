package com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;

import java.util.List;

/**
 * This class is mean to establish a connection between the docker container and the plugin
 */
public class ConnectClient {
    DockerClient dockerClient;

    public ConnectClient() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

    public String ListContainers() {
        List<Container> containers = this.dockerClient.listContainersCmd().exec();
        StringBuilder containersString = new StringBuilder();
        for (Container c : containers) {
            containersString.append(c.toString());
        }
        return containersString.toString();
    }
}
