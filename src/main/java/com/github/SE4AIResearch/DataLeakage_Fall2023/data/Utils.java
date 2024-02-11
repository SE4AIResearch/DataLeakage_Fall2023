package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Utils {

    public static String getTestFromOverlapLeakTelemetryFile() {
        String filePath = Paths.get(LeakageOutput.folderPath()).resolve("Telemetry_OverlapLeak.csv").toString();
//        File file = new File(LeakageOutput.folderPath() + "Telemetry_OverlapLeak.csv");
        File file = new File(filePath);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while (((line = reader.readLine()) != null)) {

                String[] columns = line.split(("\t"));
                var trainModel = columns[0];
                var train = columns[1];
                var trainInvo = columns[2];
                var trainMeth = columns[3];
                var ctx1 = columns[4];
                var testModel = columns[5];
                var test = columns[6];
                var invo = columns[7];
                var testMeth = columns[8];
                var ctx2 = columns[9];
                return test;

            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
        return "";
    }



    public static String getTestFromMultiUseTestLeakTelemetryFile() {
        String filePath = Paths.get(LeakageOutput.folderPath()).resolve("Telemetry_MultiUseTestLeak.csv").toString();
//        File file = new File(LeakageOutput.folderPath() + "Telemetry_MultiUseTestLeak.csv");
        File file = new File(filePath);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while (((line = reader.readLine()) != null)) {

                String[] columns = line.split(("\t"));
                var testModel = columns[0];
                var test = columns[1];
                var invo = columns[2];
                var meth = columns[3];
                var ctx1 = columns[4];
                var testModel2 = columns[5];
                var test2 = columns[6];
                var invo2 = columns[7];
                var meth2 = columns[8];
                var ctx2 = columns[9];

                return test;

            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
        return "";
    }


    public static String getTrainFromPreprocessingLeakTelemetryFile() {
        String filePath = Paths.get(LeakageOutput.folderPath()).resolve("Telemetry_PreProcessingLeak.csv").toString();
//        File file = new File(LeakageOutput.folderPath() + "Telemetry_PreProcessingLeak.csv");
        File file = new File(filePath);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while (((line = reader.readLine()) != null)) {

                String[] columns = line.split(("\t"));
                var trainModel = columns[0];
                var train = columns[1];
                var trainInvo = columns[2];
                var trainMeth = columns[3];
                var ctx1 = columns[4];
                var testModel = columns[5];
                var test = columns[6];
                var testInvo  = columns[7];
                var testMeth = columns[8];
                var ctx2 = columns[9];
                var des = columns[10];
                var src = columns[11];

                return train;

            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
        return "";
    }


}
