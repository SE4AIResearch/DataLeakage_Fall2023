package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.Invocation;
import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class LeakageDetector {
    public LeakageType leakageType;
    //TODO: remove debug
    private final boolean debug = true;

    public abstract String getCsvFileName();

    public abstract int getCsvInvocationColumn();

    public abstract void addLeakageInstance(LeakageInstance instance);

    public abstract List<LeakageInstance> leakageInstances();

    public LeakageDetector() {

    }


    public List<LeakageInstance> FindLeakageInstances(String folderPath, LeakageType leakageType) {

        try {

            File file = new File(folderPath + getCsvFileName());
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(("\t"));
                Invocation invocation = new Invocation(columns[getCsvInvocationColumn()]);
                int internalLineNumber = getInternalLineNumberFromInvocation(folderPath, invocation);
                int actualLineNumber = getActualLineNumberFromInternalLineNumber(folderPath, internalLineNumber);

                var leakageInstance = new LeakageInstance(actualLineNumber, leakageType);

                var existingInstances = leakageInstances();
                if (!debug || !existingInstances.contains(leakageInstance)) {
                    addLeakageInstance(leakageInstance);
                }


            }

            return leakageInstances();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public abstract boolean isLeakageDetected();

    private int getActualLineNumberFromInternalLineNumber(String folderPath, int internalLineNumber) {
        File file = new File(folderPath + "LinenoMapping.facts");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while (((line = reader.readLine()) != null)) {

                String[] columns = line.split(("\t"));

                if (Integer.parseInt(columns[0]) == internalLineNumber) {

                    return Integer.parseInt(columns[1]);
                }


            }

            return 0;


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getInternalLineNumberFromInvocation(String folderPath, Invocation invocation) {
        File file = new File(folderPath + "InvokeLineno.facts");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int lineNumber = invocation.getNumber() + 1;
            String line = "";

            for (int i = 1; i <= lineNumber; i++) {
                line = reader.readLine();

            }
            String[] columns = line.split(("\t"));
            return Integer.parseInt(columns[1]);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
