package com.github.SE4AIResearch.DataLeakage_Fall2023.data.telemetry;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class OverlapLeakageTelemetry {
    ;
    private String trainModel;
    private String train;
    private String trainInvo;
    private String trainMeth;
    private String ctx1;
    private String testModel;
    private String test;
    private String invo;
    private String testMeth;
    private String ctx2;

    public OverlapLeakageTelemetry() {
        String filePath = Paths.get(LeakageOutput.folderPath()).resolve("Telemetry_OverlapLeak.csv").toString();
        File file = new File(filePath);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while (((line = reader.readLine()) != null)) {

                String[] columns = line.split(("\t"));
                trainModel = columns[0];
                train = columns[1];
                trainInvo = columns[2];
                trainMeth = columns[3];
                ctx1 = columns[4];
                testModel = columns[5];
                test = columns[6];
                invo = columns[7];
                testMeth = columns[8];
                ctx2 = columns[9];

                this.test = (test);
                this.train= (train);

            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }
    public  String getTest() {
        return test;
    }

    public  String getTrain() {
        return train;
    }

}
