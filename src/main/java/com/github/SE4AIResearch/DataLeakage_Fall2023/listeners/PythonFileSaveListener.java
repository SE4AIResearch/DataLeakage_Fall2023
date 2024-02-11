package com.github.SE4AIResearch.DataLeakage_Fall2023.listeners;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.ConnectClient;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.FileChanger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class PythonFileSaveListener implements BulkFileListener {
    ConnectClient connectClient;
    FileChanger fileChanger;
    public PythonFileSaveListener() {
        this.connectClient = new ConnectClient();
        this.fileChanger = new FileChanger();
    }
    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent event : events) {
            if (event.isFromSave()) { // Check if the event is a save event
                FileType fileType = null;
                VirtualFile file = event.getFile();
                if (file != null) {
                    fileType = file.getFileType();
                }
                if (fileType != null && fileType.getName().equals("Python")) { // check that the saved file is a python file
                    // TODO: run leakage analysis code on the file
                    try {
                        connectClient.checkThenPull();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // TODO: delete previous temp directory contents

                    File tempDirectory;
                    String fileName;
                    try {
                        fileChanger.initializeTempDir();
                        tempDirectory = fileChanger.getTempDirectory();
                        fileName = fileChanger.copyToTempDir(file.getPath());
LeakageOutput.setFactFolderPath(Paths.get(tempDirectory.getCanonicalPath(),file.getNameWithoutExtension().toString()).toString()+"-fact\\");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    if (tempDirectory != null && fileName != null) {
                        connectClient.runLeakageAnalysis(tempDirectory, fileName);
                    }

                    // TODO: delete container after running

                }
            }
        }
    }

}
