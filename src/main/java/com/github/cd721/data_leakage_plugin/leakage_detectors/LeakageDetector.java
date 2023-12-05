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
    protected final boolean debug = true;

    public abstract String getCsvFileName();

    public abstract int getCsvInvocationColumn();

    public abstract void addLeakageInstance(LeakageInstance instance);

    public abstract List<LeakageInstance> leakageInstances();

    public abstract List<LeakageInstance> FindLeakageInstances(String folderPath, LeakageType leakageType);


    public LeakageDetector() {

    }


    public abstract boolean isLeakageDetected();


}
