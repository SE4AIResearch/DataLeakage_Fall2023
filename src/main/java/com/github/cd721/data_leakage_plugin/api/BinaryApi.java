package com.github.cd721.data_leakage_plugin.api;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.ModuleRootManager;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.io.FileUtilRt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BinaryApi {
    private String file_name;               // example.py
    private String file_path_inclusive;     // /path/to/file/example.py
    private String file_path;               // /path/to/file

    private String output_path;
    private String api_code_path;
    private String project_path;
    private String bin_path = "/Users/roger/Documents/1EverythingImportant/2023FallProjects/SeniorDesign/DataLeakage_Fall2023/src/main/bin/";
    private String leakage_bin_path;
    private String build_pyright_path;
    private Logger LOG;

    public BinaryApi(String fp, String projectPath) {
        Path path = Paths.get(fp);
        file_name = path.getFileName().toString();
        file_path_inclusive = fp;
        file_path = path.getParent().toString();
        output_path = projectPath + "/.out";
        project_path = projectPath;
        leakage_bin_path = bin_path + "leakage-analyisis";
        build_pyright_path = bin_path + "build-pyright";
    }

    public int buildPyright() {
        // construct command to build
        try {
            String command = build_pyright_path;
            // Create processs builder
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int conditionalBuildPyright() {
        String pyrightPath = bin_path + "pyright";
        Path directory = Paths.get(pyrightPath);

        // Pyright does not exist so build it
        if (!Files.exists(directory)) {
            return this.buildPyright();
        }

        // Pyright already present
        return 1;
    }

    public int analysis() {
        try {
            // Clean the directory, in case the user has run an analysis without cleaning
//            this.clean();

            int pyrightExitCode = this.conditionalBuildPyright();
            if (pyrightExitCode < 0) {
                return -1;
            }

            // make out path, add file to be processed to it
            File f = new File(output_path);
            if (!f.exists()) {
                // do something
                boolean out = f.mkdir();
                if (!out) {
                    return -1;
                }
            }

            // Clean the output directory
            this.clean();

            String newPath = output_path + "/runme.py";
            File source = new File(file_path_inclusive);
            File destination = new File(newPath);
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);


            // Run the leakage analysis code
            String command = leakage_bin_path;
            String args;
            if (System.getProperty("os.name").contains("Windows")) {
//                command = new String[]{"wsl", api_code_path + "/leakage-analyisis", file_path_inclusive};
                command = "wsl " + command;
                args = newPath;
            } else {
                //command = "/Users/roger/Documents/1EverythingImportant/2023FallProjects/SeniorDesign/DataLeakage_Fall2023/src/main/api/bin";
                args = newPath;
            }

            // Create ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command, args);

            // Start the process
            Process process = processBuilder.start();

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder consoleOut = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                consoleOut.append(line);
            }
            LOG.info(consoleOut.toString());

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

            return exitCode;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void delete(String stringToExclude) {
        File directory = new File(output_path);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Invalid directory path");
            return;
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().contains(stringToExclude) && !file.getName().equals(file_name)) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    public void clean() {
        this.delete(file_name.replaceAll("\\.py$", ""));
    }

    public static void main(String[] args) {
        // BinaryApi example = new BinaryApi("../tests/test_overlap.py");
        // example.clean();
    }
}