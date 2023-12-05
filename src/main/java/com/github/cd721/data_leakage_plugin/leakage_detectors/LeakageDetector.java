package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.Invocation;
import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.data.LeakageOutput;
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
    protected final boolean debug = true;

    public abstract String getCsvFileName();

    public abstract int getCsvInvocationColumn();

    public abstract void addLeakageInstance(LeakageInstance instance);

    public abstract List<LeakageInstance> leakageInstances();

    public List<LeakageInstance> FindLeakageInstances( LeakageType leakageType3) {



            File file = new File(LeakageOutput.folderPath() + this.getCsvFileName());

            findLeakageInstancesInFile(file);

            return leakageInstances();


    }

    private void findLeakageInstancesInFile(File file) {
        try {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] columns = line.split(("\t"));
            Invocation invocation = new Invocation(columns[getCsvInvocationColumn()]);
            int internalLineNumber = Utils.getInternalLineNumberFromInvocation(LeakageOutput.folderPath(), invocation);
            int actualLineNumber = Utils.getActualLineNumberFromInternalLineNumber(LeakageOutput.folderPath(), internalLineNumber);

            var leakageInstance = new LeakageInstance(actualLineNumber, leakageType, invocation);

            var existingInstances = leakageInstances();
            if (!debug || !existingInstances.contains(leakageInstance)) {
                addLeakageInstance(leakageInstance);
            }


        }  } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public LeakageDetector() {

    }


    public abstract boolean isLeakageDetected();


}
