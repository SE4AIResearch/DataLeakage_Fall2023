package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.LeakageOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
    public static int myFunc( ) {
        File file = new File(LeakageOutput.folderPath() + "Telemetry_MultiUseTestLeak.csv");
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
                var invo2= columns[7];
                var meth2 = columns[8];
                var ctx2 = columns[9];



            }


        } catch (IOException e) {
            e.printStackTrace();

        }
        return 0;
    }
}
