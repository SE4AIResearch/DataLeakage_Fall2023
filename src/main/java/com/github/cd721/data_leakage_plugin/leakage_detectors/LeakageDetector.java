package com.github.cd721.data_leakage_plugin.leakage_detectors;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.data.LeakageOutput;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;

import java.io.File;
import java.util.List;

public abstract class LeakageDetector {
    public LeakageType leakageType;
    //TODO: remove debug
    protected final boolean debug = true;

    public abstract String getCsvFileName();

    public abstract int getCsvInvocationColumn();

    public abstract void addLeakageInstance(LeakageInstance instance);

    public abstract List<LeakageInstance> leakageInstances();

    public List<LeakageInstance> FindLeakageInstances() {



            File file = new File(LeakageOutput.folderPath() + this.getCsvFileName());

            findLeakageInstancesInFile(file);

            return leakageInstances();


    }

    public abstract void findLeakageInstancesInFile(File file) ;

    public LeakageDetector() {

    }


    public abstract boolean isLeakageDetected();


}
