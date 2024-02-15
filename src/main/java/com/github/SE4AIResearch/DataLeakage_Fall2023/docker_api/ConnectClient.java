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
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.ThrowableComputable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Import java libraries
import javax.swing.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * This class creates a Docker client to connect to the hosts docker daemon
 */
public class ConnectClient extends ProgressManager {

    private static DockerClient dockerClient;

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
    }

    @Override
    public boolean hasProgressIndicator() {
        return false;
    }

    @Override
    public boolean hasModalProgressIndicator() {
        return false;
    }

    @Override
    public boolean hasUnsafeProgressIndicator() {
        return false;
    }

    @Override
    public void runProcess(@NotNull Runnable process, @Nullable ProgressIndicator progress) throws ProcessCanceledException {

    }

    @Override
    public ProgressIndicator getProgressIndicator() {
        return null;
    }

    @Override
    protected void doCheckCanceled() throws ProcessCanceledException {

    }

    @Override
    public void executeNonCancelableSection(@NotNull Runnable runnable) {

    }

    @Override
    public <T, E extends Exception> T computeInNonCancelableSection(@NotNull ThrowableComputable<T, E> computable) throws E {
        return null;
    }

    @Override
    public boolean runProcessWithProgressSynchronously(@NotNull Runnable process, @NotNull @NlsContexts.DialogTitle String progressTitle, boolean canBeCanceled, @Nullable Project project) {
        return false;
    }

    @Override
    public <T, E extends Exception> T runProcessWithProgressSynchronously(@NotNull ThrowableComputable<T, E> process, @NotNull @NlsContexts.DialogTitle String progressTitle, boolean canBeCanceled, @Nullable Project project) throws E {
        return null;
    }

    @Override
    public boolean runProcessWithProgressSynchronously(@NotNull Runnable process, @NotNull @NlsContexts.DialogTitle String progressTitle, boolean canBeCanceled, @Nullable Project project, @Nullable JComponent parentComponent) {
        return false;
    }

    @Override
    public void runProcessWithProgressAsynchronously(@NotNull Project project, @NotNull @NlsContexts.ProgressTitle String progressTitle,
                                                     @NotNull Runnable process, @Nullable Runnable successRunnable, @Nullable Runnable canceledRunnable,
                                                     @NotNull PerformInBackgroundOption option) {

    }

    @Override
    public void run(@NotNull Task task) {

    }

    @Override
    public void runProcessWithProgressAsynchronously(Task.@NotNull Backgroundable task, @NotNull ProgressIndicator progressIndicator) {

    }

    @Override
    public void executeProcessUnderProgress(@NotNull Runnable process, @Nullable ProgressIndicator progress) throws ProcessCanceledException {

    }

    @Override
    public boolean runInReadActionWithWriteActionPriority(@NotNull Runnable action, @Nullable ProgressIndicator indicator) {
        return false;
    }

    @Override
    public boolean isInNonCancelableSection() {
        return false;
    }

    @Override
    public <T, E extends Throwable> T computePrioritized(@NotNull ThrowableComputable<T, E> computable) throws E {
        return null;
    }

    @Override
    public <X> X silenceGlobalIndicator(@NotNull Supplier<? extends X> computable) {
        return null;
    }

    @Override
    public @Nullable ModalityState getCurrentProgressModality() {
        return null;
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
     * Checks for the image and pulls if it is not on the machine
     *
     * @return Boolean - True on successful pull. False if failed
     * */
    public boolean checkThenPull() throws InterruptedException {
        if (!this.checkImageOnMachine()) {
            this.pullImage();
        }
        return true;
    }



}
