package com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public abstract class LeakageDetector {
    public LeakageType leakageType;

    /**
     * @return The name of the CSV file that contains relevant information
     * about the leakage of {@link #leakageType} of this detector.
     */
    public abstract String getCsvFileName();

    /**
     * @return The column of the CSV file (specified by {@link #getCsvFileName()}) that
     * contains the {@link Invocation} associated with this detector's {@link #leakageType}.
     */
    public abstract int getCsvInvocationColumn();

    /**
     * Adds a {@code LeakageInstance} to this detector's {@code leakageInstances()}.
     *
     * @param instance The {@link LeakageInstance} to add.
     */
    public abstract void addLeakageInstance(LeakageInstance instance);

    /**
     * @return A {@link List<LeakageInstance>} representing containing the {@link LeakageInstance}s caught by this detector.
     */
    public abstract List<LeakageInstance> leakageInstances();

    public List<LeakageInstance> FindLeakageInstances() {

        String filePath = Paths.get(LeakageOutput.folderPath()).resolve(this.getCsvFileName()).toString();
        File file = new File(filePath);

        findLeakageInstancesInFile(file);

        return leakageInstances();


    }

    public abstract void findLeakageInstancesInFile(File file);

    public LeakageDetector() {

    }

    public abstract boolean isLeakageDetected();

}
