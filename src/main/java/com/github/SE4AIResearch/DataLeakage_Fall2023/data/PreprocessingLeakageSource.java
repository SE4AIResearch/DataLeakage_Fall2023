package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.TaintUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.LeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.PreprocessingLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.taints.TaintLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PreprocessingLeakageSource extends LeakageSource {
    private PreprocessingLeakageSourceKeyword leakageSourceKeyword;

    private List<Taint> taints;
    private List<Integer> lineNumbers;
    private LeakageType type;

    public PreprocessingLeakageSource() {
        initializeTaints();
        this.lineNumbers = setLineNumbers();
    }

    @Override
    public LeakageSourceKeyword getLeakageSourceKeyword() {
        return leakageSourceKeyword;
    }

    @Override
    public List<LeakageCause> getPotentialCauses() {
        return switch (this.leakageSourceKeyword) {

            case predict -> List.of(LeakageCause.VectorizingTextData);

        };
    }

    @Override
    public void setLineNumbers(List<Integer> lineNumbers) {
        this.lineNumbers = lineNumbers;
    }

    @Override
    public List<Taint> getTaints() {
        return null;
    }


    @Override
    public List<Integer> getLineNumbers() {
        return null;
    }

    @Override
    public void setTaints(List<Taint> newTaints) {
        this.taints = newTaints;
    }


    private void initializeTaints() {
        taints = TaintUtils.getTaintsFromFile(TaintLabel.rowset).stream()
                .map(taintString -> new Taint(taintString, TaintLabel.rowset))
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
