package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.LeakageSourceKeyword;

import java.util.ArrayList;
import java.util.List;

public class MultiTestLeakageSource extends LeakageSource {
    private List<Taint> taints;
    private List<Integer> lineNumbers;

    public MultiTestLeakageSource() {
        this.taints = new ArrayList<>();
        this.lineNumbers = new ArrayList<>();
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
