package com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors;

import com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
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

public class OverlapLeakageDetector extends LeakageDetector<OverlapLeakageInstance> {
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
    protected void addLeakageInstanceIfNotPresent(OverlapLeakageInstance leakageInstance) {

        var existingInstances = leakageInstances();
        if (!existingInstances.contains(leakageInstance)) {
            if (!anyLinesAreOnExclusionList(leakageInstance)) {
                addLeakageInstance(leakageInstance);
            }
        }
    }

    private boolean anyLinesAreOnExclusionList(LeakageInstance leakageInstance) {
        List<Integer> linesOnExlcusionList = Utils.linesOnExclusionList();

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

    @NotNull
    @Override
    protected OverlapLeakageInstance createLeakageInstanceFromLine(String line) {
        String[] columns = line.split(("\t"));

        final var leakageFinal = new OverlapLeakageFinal(columns);
        final var telemetry = new OverlapLeakageTelemetry(leakageFinal);

        Invocation invocation = new Invocation(leakageFinal.getInvo());
        int internalLineNumber = Invocation.getInternalLineNumberFromInvocation(LeakageResult.getFolderPath(), invocation);
        int actualLineNumber = getActualLineNumberFromInternalLineNumber(LeakageResult.getFolderPath(), internalLineNumber);


        return new OverlapLeakageInstance(actualLineNumber, invocation,
                telemetry.getTest(), telemetry.getTrain());
    }


    @Override
    public String getCsvFileName() {
        return "FinalOverlapLeak.csv";
    }
}
