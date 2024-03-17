package com.github.SE4AIResearch.DataLeakage_Fall2023.data.finals;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.telemetry.OverlapLeakageTelemetry;
import org.jetbrains.annotations.NotNull;

public class OverlapLeakageFinal implements Comparable<OverlapLeakageTelemetry>, OverlapLeakageData{

    private String trainModel;
    private String train;
    private String invo;
    private
    String trainMeth;
    private String ctx;
    private String cnt;

    public OverlapLeakageFinal(String trainModel,
                               String train,
                               String invo,
                               String trainMeth,
                               String ctx,
                               String cnt) {
        this.trainModel = trainModel;
        this.train = train;
        this.invo = invo;
        this.trainMeth = trainMeth;
        this.ctx = ctx;
        this.cnt = cnt;
    }

    public boolean matches(OverlapLeakageTelemetry telemetry){
        return this.trainMeth.equals(telemetry.getTrainMeth())
                &&
    }

    @Override
    public String getTrainModel() {
        return trainModel;
    }

    @Override
    public String getTrain() {
        return train;
    }

    @Override
    public String getInvo() {
        return invo;
    }

    @Override
    public String getTrainMeth() {
        return trainMeth;
    }

    @Override
    public String getCtx() {
        return ctx;
    }

    @Override
    public String getCnt() {
        return cnt;
    }



    @Override
    public int compareTo(@NotNull OverlapLeakageTelemetry o) {
        return 0;
    }
}
