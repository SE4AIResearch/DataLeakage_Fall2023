package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

/**
 * This record contains the absolute path to the output of the leakage analysis tool. Currently, this path
 * must be changed each time you want to test the plugin on a new Python file.
 * //TODO: make the path point to the output of the leakage tool binary.
 */
public  class LeakageOutput {
    private static String folderPath =  "C:\\dev\\nb_100841-fact\\";;
    public static String folderPath() {
        return folderPath;
    }

    public static void setFactFolderPath(String path){

        folderPath = path;
    }

}
