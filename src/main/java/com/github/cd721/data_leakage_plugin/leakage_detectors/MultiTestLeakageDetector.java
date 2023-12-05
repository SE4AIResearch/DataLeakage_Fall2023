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

public class MultiTestLeakageDetector extends LeakageDetector {
    private final List<LeakageInstance> leakageInstances;

    @Override
    public String getCsvFileName() {
        return "MultiUseTestLeak.csv";
    }

    @Override
    public int getCsvInvocationColumn() {
        return 1;
    }

    @Override
    public void addLeakageInstance(LeakageInstance instance) {
        this.leakageInstances.add(instance);
    }

    @Override
    public List<LeakageInstance> leakageInstances() {
        return leakageInstances;
    }

    public MultiTestLeakageDetector() {
        super();
        this.leakageType = LeakageType.MultiTestLeakage;
        this.leakageInstances = new ArrayList<>();
    }


    @Override
    public boolean isLeakageDetected() {
        return !this.leakageInstances.isEmpty();
    }

    @Override
    public List<LeakageInstance> FindLeakageInstances(String folderPath, LeakageType leakageType) {

        try {

            File file = new File(folderPath + this.getCsvFileName());
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(("\t"));
                Invocation invocation = new Invocation(columns[getCsvInvocationColumn()]);
                int internalLineNumber = Utils.getInternalLineNumberFromInvocation(folderPath, invocation);
                int actualLineNumber = Utils.getActualLineNumberFromInternalLineNumber(folderPath, internalLineNumber);

                var leakageInstance = new LeakageInstance(actualLineNumber, leakageType, invocation);

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
}
