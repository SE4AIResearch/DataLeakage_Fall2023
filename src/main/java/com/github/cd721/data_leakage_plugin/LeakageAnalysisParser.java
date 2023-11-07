package com.github.cd721.data_leakage_plugin;

import java.io.*;

public class LeakageAnalysisParser {
    private boolean overlapLeakageDetected;

    public int OverlapLeakageInstances() {
        int count = 0;
        try {

            File file = new File("C:/dev/paper-sample-2-fact/FinalOverlapLeak.csv");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.readLine() != null) {
                count++;
            }

            return count;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isOverlapLeakageDetected() {
        return OverlapLeakageInstances() > 0;
    }
}
