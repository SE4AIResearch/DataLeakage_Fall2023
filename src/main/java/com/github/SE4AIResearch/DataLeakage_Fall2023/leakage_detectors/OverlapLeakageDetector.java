package com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.finals.OverlapLeakageFinal;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.telemetry.OverlapLeakageTelemetry;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils.getActualLineNumberFromInternalLineNumber;

public class OverlapLeakageDetector extends LeakageDetector {
    private final List<LeakageInstance> leakageInstances;

    public OverlapLeakageDetector() {
        super();
        this.leakageType = LeakageType.OverlapLeakage;
        this.leakageInstances = new ArrayList<>();
    }


    @Override
    public boolean isLeakageDetected() {
        return !this.leakageInstances.isEmpty();
    }


    @Override
    public int getCsvInvocationColumn() {
        return 2;
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
                    var leakageInstance = createLeakageInstanceFromLine(line);

                    addLeakageInstanceIfNotPresent(leakageInstance);

                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addLeakageInstanceIfNotPresent(LeakageInstance leakageInstance) {


        var existingInstances = leakageInstances();
        if (!existingInstances.contains(leakageInstance)) {
            addLeakageInstance(leakageInstance);
        }
    }

    @NotNull
    private OverlapLeakageInstance createLeakageInstanceFromLine(String line) {
        String[] columns = line.split(("\t"));
        Invocation invocation = new Invocation(columns[getCsvInvocationColumn()]);
        int internalLineNumber = Invocation.getInternalLineNumberFromInvocation(LeakageOutput.folderPath(), invocation);
        int actualLineNumber = getActualLineNumberFromInternalLineNumber(LeakageOutput.folderPath(), internalLineNumber);

        final var leakageFinal = new OverlapLeakageFinal(columns);
        final var telemetry = new OverlapLeakageTelemetry(leakageFinal);

        return new OverlapLeakageInstance(actualLineNumber, invocation,
                telemetry.getTest(), telemetry.getTrain());
    }


    @Override
    public String getCsvFileName() {
        return "FinalOverlapLeak.csv";
    }
}
