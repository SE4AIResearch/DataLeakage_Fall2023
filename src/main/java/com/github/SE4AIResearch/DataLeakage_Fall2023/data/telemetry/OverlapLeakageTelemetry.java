package com.github.SE4AIResearch.DataLeakage_Fall2023.data.telemetry;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.finals.OverlapLeakageData;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class OverlapLeakageTelemetry implements OverlapLeakageData {
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
                this.trainModel = columns[0];
                this.train = columns[1];
                this.trainInvo = columns[2];
                this.trainMeth = columns[3];
                this.ctx1 = columns[4];
                this.testModel = columns[5];
                this.test = columns[6];
                this.invo = columns[7];
                this.testMeth = columns[8];
                this.ctx2 = columns[9];


            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public String getTest() {
        return test;
    }

    @Override
    public String getTrainModel() {
        return trainModel
    }

    public String getTrain() {
        return train;
    }

    @Override
    public String getInvo() {
        return invo;
    }

    @Override
    public String getTrainMeth() {
        return trainMeth;
    }

    @Override
    public String getCtx() {
        return ctx;
    }

    @Override
    public String getCnt() {
        return cnt;
    }

    @Override
    public int compareTo(@NotNull OverlapLeakageData o) {
        return 0;
    }
}
