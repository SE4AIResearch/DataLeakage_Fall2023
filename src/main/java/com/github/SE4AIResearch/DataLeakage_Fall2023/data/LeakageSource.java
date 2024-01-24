package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.TaintUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.TaintLabel;

import java.util.List;
import java.util.stream.Collectors;


/**
 * The source of particular instance of data leakage exists on multiple {@link #lineNumbers} and is associated with
 * a {@link #taints}.
 */

public class LeakageSource {

    private final List<Taint> taints;
    private final List<Integer> lineNumbers;
    private final LeakageCause cause;

    public LeakageSource(LeakageType leakageType) {
        this.taints = setTaints(leakageType);
        this.cause = setCause();
        this.lineNumbers = setLineNumbers();


    }

    public LeakageCause getCause() {
        return cause;
    }
    private LeakageCause setCause() {
        //TODO: revise
        if(taints.stream().anyMatch(taint -> taint.getPyCallExpression().toLowerCase().contains("vector"))){
            return LeakageCause.VectorizingTextData;
        } else if(taints.stream().allMatch(taint -> taint.getPyCallExpression().toLowerCase().contains("split"))){
            return LeakageCause.SplitBeforeSample;
        } else if (taints.stream().allMatch(taint -> taint.getPyCallExpression().contains("TODO"))) {
        return LeakageCause.DataAugmentation;
        }
        return LeakageCause.unknown;
    }

    private List<Integer> setLineNumbers() {
        return taints.stream().map(taint ->
                Utils.getActualLineNumberFromInternalLineNumber(LeakageOutput.folderPath(),
                        Invocation.getInternalLineNumberFromInvocation(LeakageOutput.folderPath(),
                                taint.getInvocation()))

        ).collect(Collectors.toList());


    }

    public List<Taint> getTaints() {
        return taints;
    }

    private List<Taint> setTaints(LeakageType leakageType) {
        return switch (leakageType) {
            case OverlapLeakage -> TaintUtils.getTaintsFromFile(TaintLabel.dup).stream()
                    .map(taintString -> new Taint(taintString, TaintLabel.dup))
                    .collect(Collectors.toList());
            case PreprocessingLeakage -> TaintUtils.getTaintsFromFile(TaintLabel.rowset).stream()
                    .map(taintString -> new Taint(taintString, TaintLabel.rowset))
                    .collect(Collectors.toList());

            default -> throw new IllegalStateException("Unexpected value: " + leakageType);
        };
    }

    public Taint findTaintThatMatchesText(String text) {
        return this.getTaints().stream().filter(
                taint -> taint.getPyCallExpression().equalsIgnoreCase(text) //equalsIgnoreCase MUST be used here
        ).findFirst().orElse(new Taint("", TaintLabel.dup));//TODO: better error handling
    }


    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }


}
