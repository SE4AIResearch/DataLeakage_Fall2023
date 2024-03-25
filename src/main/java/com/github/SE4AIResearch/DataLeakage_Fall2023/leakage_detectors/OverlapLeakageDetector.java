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
import java.nio.file.Paths;
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

                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addLeakageInstanceIfNotPresent(LeakageInstance leakageInstance) {

        var existingInstances = leakageInstances();
        if (!existingInstances.contains(leakageInstance)) {
            if (!anyLinesAreOnExclusionList(leakageInstance)) {
                addLeakageInstance(leakageInstance);
            }
        }
    }

    private boolean anyLinesAreOnExclusionList(LeakageInstance leakageInstance) {
        List<Integer> linesOnExlcusionList = linesOnExclusionList();

        if (linesOnExlcusionList.contains(leakageInstance.lineNumber())) {
            return true;
        }

        var source = leakageInstance.getLeakageSource();

        for (Integer lineNo : source.getLineNumbers()) {
            if (linesOnExlcusionList.contains(lineNo)) {
                return true;
            }
        }

        return false;
    }

    private List<Integer> linesOnExclusionList() {
        String exclusionFilePath = Paths.get(LeakageOutput.folderPath()).resolve(LeakageOutput.getExclusionFileName()).toString();
        File file = new File(exclusionFilePath);


        List<Integer> linesToExclude = new ArrayList<>();
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        linesToExclude.add(Integer.parseInt(line.strip()));
                    } catch (NumberFormatException e) {
                        //ignore
                    }


                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return linesToExclude;
    }

    @NotNull
    private OverlapLeakageInstance createLeakageInstanceFromLine(String line) {
        String[] columns = line.split(("\t"));

        final var leakageFinal = new OverlapLeakageFinal(columns);
        final var telemetry = new OverlapLeakageTelemetry(leakageFinal);

        Invocation invocation = new Invocation(leakageFinal.getInvo());
        int internalLineNumber = Invocation.getInternalLineNumberFromInvocation(LeakageOutput.folderPath(), invocation);
        int actualLineNumber = getActualLineNumberFromInternalLineNumber(LeakageOutput.folderPath(), internalLineNumber);


        return new OverlapLeakageInstance(actualLineNumber, invocation,
                telemetry.getTest(), telemetry.getTrain());
    }


    @Override
    public String getCsvFileName() {
        return "FinalOverlapLeak.csv";
    }
}
