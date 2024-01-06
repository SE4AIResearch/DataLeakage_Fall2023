package com.github.cd721.data_leakage_plugin.data.leakage_data;

import com.github.cd721.data_leakage_plugin.enums.TaintLabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaintUtils {

    public static List<String> getTaintsFromFile(TaintLabel taintType) {
        File file = new File(LeakageOutput.folderPath() + "TaintStartsTarget.csv");
        List<String> taints = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while (((line = reader.readLine()) != null)) {
                String[] columns = line.split(("\t"));
                if (Objects.equals(columns[6], taintType.toString())) {
                    taints.add( line);
                }

            }


        } catch (IOException e) {
            e.printStackTrace();

        }
        return taints;
    }
}
