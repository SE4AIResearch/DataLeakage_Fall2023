package com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api;

import com.intellij.openapi.util.io.FileUtilRt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileChanger {
    File workingDirectory;
    public FileChanger() {
        this.workingDirectory = null;
    }

    public String initializeTempDir() throws IOException {
        workingDirectory = FileUtilRt.createTempDirectory("leakTemp", "");
        return this.workingDirectory.toString();
    }

    public String copyToTempDir(String filePathString) throws IOException {
        File fileToCopy = Paths.get(filePathString).toFile();
        String fileName = fileToCopy.getName();
        File destinationFile = FileUtilRt.createTempFile(workingDirectory, fileName, "");
        FileUtilRt.copy(fileToCopy, destinationFile);
        return fileName;
    }

    public boolean deleteTempDir() throws IOException {
        if (workingDirectory == null) {
            return false;
        }
        Path workingDirPath = this.workingDirectory.toPath();
        FileUtilRt.deleteRecursively(workingDirPath);
        return FileUtilRt.delete(this.workingDirectory);
    }

    public boolean deleteAllTempDir() throws IOException {
        Path workingDirPath = this.workingDirectory.toPath();
        Path parentPath = workingDirPath.getParent();
        return false;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }
}
