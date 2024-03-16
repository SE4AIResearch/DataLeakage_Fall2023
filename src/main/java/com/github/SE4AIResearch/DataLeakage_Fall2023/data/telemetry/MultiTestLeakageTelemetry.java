package com.github.SE4AIResearch.DataLeakage_Fall2023.data.telemetry;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class MultiTestLeakageTelemetry {

    private String test;

    public MultiTestLeakageTelemetry(){
        String filePath = Paths.get(LeakageOutput.folderPath()).resolve("Telemetry_MultiUseTestLeak.csv").toString();
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

                this .test= Utils.stripSuffixFromVariableName(test);


            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
        this.test= "";
    }

    public String getTest() {
        return this.test;
    }
}
