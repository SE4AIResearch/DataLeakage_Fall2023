package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
import com.intellij.openapi.util.io.FileUtilRt;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InspectionUtils {
    public static void addLinesToExclusion(List<Integer> lines) {
        // File destinationFile = new File(String.valueOf(Paths.get(LeakageResult.getFolderPath()).resolve(LeakageOutput.getExclusionFileName())));


        String exclusionFilePath = Paths.get(LeakageResult.getFolderPath()).resolve(LeakageResult.getExclusionFileName()).toString();
        File exclusionFile = new File(exclusionFilePath);

        FileUtilRt.createIfNotExists(exclusionFile);


        try {
            FileWriter fr = new FileWriter(exclusionFile.getPath(), true);
            for (var line : lines) {
                fr.write(line.toString());
                fr.write("\n");

            }
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean anyLinesAreOnExclusionList(int nodeLineNumber) {
        List<Integer> linesOnExlcusionList = linesOnExclusionList();


        return linesOnExlcusionList.contains(nodeLineNumber);
    }
    public static boolean anyLinesAreOnExclusionList(LeakageInstance leakageInstance, int nodeLineNumber) {
        List<Integer> linesOnExlcusionList = linesOnExclusionList();

        if (linesOnExlcusionList.contains(leakageInstance.lineNumber())) {
            return true;
        }
        if (linesOnExlcusionList.contains(nodeLineNumber)) {
            return true;
        }

        var source = leakageInstance.getLeakageSource();

        for (Integer lineNo : source.getLineNumbers()) {
            if (linesOnExlcusionList.contains(lineNo)) {
                return true;
            }
        }

        return false;
    }

    protected static List<Integer> linesOnExclusionList() {
        String exclusionFilePath = Paths.get(LeakageResult.getFolderPath()).resolve(LeakageResult.getExclusionFileName()).toString();
        File file = new File(exclusionFilePath);


        List<Integer> linesToExclude = new ArrayList<>();
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        linesToExclude.add(Integer.parseInt(line.strip()));
                    } catch (NumberFormatException e) {
                        //ignore
                    }


                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return linesToExclude;
    }


}

