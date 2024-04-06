package com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors;

import com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreprocessingLeakageDetector extends LeakageDetector {
    private final List<LeakageInstance> leakageInstances;

    public PreprocessingLeakageDetector() {
        super();
        this.leakageType = LeakageType.PreprocessingLeakage;
        this.leakageInstances = new ArrayList<>();
    }

    @Override
    public String getCsvFileName() {
        return "PreProcessingLeak.csv";
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

    @Override
    public void findLeakageInstancesInFile(File file) {
        if (file.exists()) {

            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] columns = line.split(("\t"));
                    Invocation invocation = new Invocation(columns[getCsvInvocationColumn()]);
                    int internalLineNumber = Invocation.getInternalLineNumberFromInvocation(LeakageResult.getFolderPath(), invocation);
                    int actualLineNumber = Utils.getActualLineNumberFromInternalLineNumber(LeakageResult.getFolderPath(), internalLineNumber);

                    var leakageInstance = new PreprocessingLeakageInstance(actualLineNumber, invocation);

                    var existingInstances = leakageInstances();
                    if (!existingInstances.contains(leakageInstance)) {
                        addLeakageInstance(leakageInstance);

                    }


                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isLeakageDetected() {
        return !this.leakageInstances.isEmpty();
    }

}
