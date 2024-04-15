package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.LeakageSourceKeyword;

import java.util.ArrayList;
import java.util.List;

public class MultiTestLeakageSource extends LeakageSource {
    private List<Taint> taints;
    private List<Integer> lineNumbers;
    private final LeakageCause cause;

    public MultiTestLeakageSource() {
        this.cause = LeakageCause.RepeatDataEvaluation;
        this.taints = new ArrayList<>();
        this.lineNumbers = new ArrayList<>();
    }




    @Override
    public LeakageSourceKeyword getLeakageSourceKeyword() {
        return null;//TODO: better way of doing this?
    }

    @Override
    public List<LeakageCause> getPotentialCauses() {
        return List.of(LeakageCause.RepeatDataEvaluation);
    }



    @Override
    public void setLineNumbers(List<Integer> lineNumbers) {
        this.lineNumbers = lineNumbers;
    }

    @Override
    public List<Taint> getTaints() {
        return taints;
    }

    @Override
    public void setTaints(List<Taint> newTaints) {
        this.taints = newTaints;
    }

    @Override
    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }
}
