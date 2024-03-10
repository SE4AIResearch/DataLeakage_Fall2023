package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.TaintUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType.PreprocessingLeakage;


/**
 * The source of particular instance of data leakage exists on multiple {@link #lineNumbers} and is associated with
 * a {@link #taints}.
 */

public class LeakageSource {

    private final List<Taint> taints;
    private final List<Integer> lineNumbers;
    private final LeakageCause cause;

    public LeakageSource(LeakageType leakageType) {
        this.taints = setTaints(leakageType).stream().distinct().toList();
        this.lineNumbers = setLineNumbers();
        this.cause = setCause(leakageType);



    }

    public LeakageCause getCause() {
        return cause;
    }

    private LeakageCause setCause(LeakageType leakageType) {
        //TODO: revise
        if (taints.stream().anyMatch(taint -> taint.containsText("vector"))) {
            return LeakageCause.VectorizingTextData;
        } else if (taints.stream().allMatch(taint -> taint.containsText("split") || taint.containsText("sample"))) {
            return LeakageCause.SplitBeforeSample;
        } else if (taints.stream().allMatch(taint -> taint.containsText("flow"))) {
            return LeakageCause.DataAugmentation;
        } else if (taints.stream().allMatch(taint -> taint.containsText("select"))) {
            return LeakageCause.UsingTestDataForFeatureSelection;
        }

//        if (taints.stream().allMatch(taint -> Arrays.stream(getKeyword(leakageType))
//                .anyMatch(text -> taint.containsText(text.getTaintKeyword()))))
//        {
//            return Arrays.stream(getKeyword(leakageType))
//                    .filter(leakageSourceKeyword ->
//                            taints.stream().allMatch(taint ->
//                                    taint.containsText(leakageSourceKeyword.getTaintKeyword()))).findFirst()
//                    .get().getPotentialCauses().get(0);
//
//        }

        return LeakageCause.unknown;
    }

    private LeakageSourceKeyword[] getKeyword(LeakageType type) {
        if (type.equals(PreprocessingLeakage)) {
            return PreprocessingLeakageSourceKeyword.values();
        }
        return OverlapLeakageSourceKeyword.values();
    }

    private List<Integer> setLineNumbers() {
        return taints.stream().map(taint -> Utils.getActualLineNumberFromInternalLineNumber(LeakageOutput.folderPath(), Invocation.getInternalLineNumberFromInvocation(LeakageOutput.folderPath(), taint.getInvocation()))

        ).collect(Collectors.toList());


    }

    public List<Taint> getTaints() {
        return taints;
    }

    private List<Taint> setTaints(LeakageType leakageType) {
        List<Taint> taints;
        switch (leakageType) {
            case OverlapLeakage ->
                    taints = TaintUtils.getTaintsFromFile(TaintLabel.dup).stream().map(taintString -> new Taint(taintString, TaintLabel.dup)).collect(Collectors.toList());
            case PreprocessingLeakage ->
                    taints = TaintUtils.getTaintsFromFile(TaintLabel.rowset).stream().map(taintString -> new Taint(taintString, TaintLabel.rowset)).collect(Collectors.toList());

            default -> taints = new ArrayList<>();
        }
        List<Taint> rtn = new ArrayList<>();//TODO: .distinct() method doesn't work for taints
        for (var taint : taints) {
            if (!rtn.contains(taint)) {
                rtn.add(taint);
            }
        }
        return rtn;
    }

    public Taint findTaintThatMatchesText(String text) {
        return this.getTaints().stream().filter(taint -> taint.getPyCallExpression().equalsIgnoreCase(text) //equalsIgnoreCase MUST be used here
                //TODO: the pycall expression in the leakage analysis tool does not match what is actually in the test file
        ).findFirst().orElse(new Taint("", TaintLabel.dup));//TODO: better error handling
    }


    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }


}
