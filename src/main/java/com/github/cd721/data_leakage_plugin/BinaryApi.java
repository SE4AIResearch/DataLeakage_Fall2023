package com.github.cd721.data_leakage_plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BinaryApi {
    String file_path;
    public BinaryApi(String fp) {
        file_path = fp;
    }

    public int analysis() {
        try {
            // Run the leakage analysis code
            String[] command = {"../bin/run-me", file_path};

            // Create ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Start the process
            Process process = processBuilder.start();

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

            return exitCode;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

//    private void removeFilesExceptOne(String directoryPath, String fileToKeep) {
//        File directory = new File(directoryPath);
//
//        // Check if the directory exists
//        if (!directory.exists() || !directory.isDirectory()) {
//            System.out.println("Invalid directory path.");
//            return;
//        }
//
//        // Get all files and subdirectories in the directory
//        File[] files = directory.listFiles();
//
//        // Iterate through the files and delete them (recursively for directories) except for the specified file
//        if (files != null) {
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    // Recursively delete contents of the directory
//                    removeFilesExceptOne(file.getAbsolutePath(), fileToKeep);
//                } else if (!file.getName().equals(fileToKeep)) {
//                    if (!file.delete()) {
//                        System.out.println("Failed to delete: " + file.getAbsolutePath());
//                    }
//                }
//            }
//        }
//
//        // Delete the empty directory after its contents are deleted
//        if (!directory.getAbsolutePath().equals(fileToKeep)) {
//            if (!directory.delete()) {
//                System.out.println("Failed to delete directory: " + directory.getAbsolutePath());
//            }
//        }
//    }

//    public void clean() {
//        Path path = Paths.get(file_path);
//
//        String directoryPath = path.getParent().toString();
//        String fileToKeep = path.getFileName().toString();
//
//        this.removeFilesExceptOne(directoryPath, fileToKeep);
//    }

//    public static void main(String[] args) {
//        BinaryApi example = new BinaryApi("../tests/test_overlap.py");
//        example.analysis();
//        example.clean();
//    }
}