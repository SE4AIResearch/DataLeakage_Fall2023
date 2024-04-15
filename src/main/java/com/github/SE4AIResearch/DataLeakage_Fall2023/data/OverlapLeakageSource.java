package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.TaintUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.taints.TaintLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OverlapLeakageSource extends LeakageSource {
    private OverlapLeakageSourceKeyword leakageSourceKeyword;

    private List<Taint> taints;
    private List<Integer> lineNumbers;

    public OverlapLeakageSource() {
        intializeTaints();
        this.lineNumbers = setLineNumbers();
    }

    @Override
    public OverlapLeakageSourceKeyword getLeakageSourceKeyword() {
        return leakageSourceKeyword;
    }

    @Override
    public List<LeakageCause> getPotentialCauses() {
        return switch (this.leakageSourceKeyword) {
            case flow -> List.of(LeakageCause.DataAugmentation);
            case sample -> List.of(LeakageCause.SplitBeforeSample);
        };
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
    public List<Integer> getLineNumbers() {
        return this.lineNumbers;
    }

    @Override
    public void setTaints(List<Taint> newTaints) {

    }


    private void intializeTaints() {
        this.taints = TaintUtils.getTaintsFromFile(TaintLabel.dup).stream()
                .map(taintString -> new Taint(taintString, TaintLabel.dup))
                .collect(Collectors.toList());
        List<Taint> rtn = new ArrayList<>();//TODO: .distinct() method doesn't work for taints

        for (var taint : taints) {
            if (!rtn.contains(taint)) {
                rtn.add(taint);
            }
        }
        this.taints = this.taints.stream().distinct().toList();//TODO: refactor
    }


}
