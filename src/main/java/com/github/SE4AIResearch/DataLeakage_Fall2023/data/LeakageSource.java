package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.TaintUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.taints.TaintLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The source of particular instance of data leakage exists on multiple {@link #lineNumbers} and is associated with
 * a {@link #taints}.
 */

public class LeakageSource {
    private final List<Taint> taints;
    private List<Integer> lineNumbers;
    private final LeakageCause cause;
    private final LeakageType type;

    public LeakageSource(LeakageType leakageType) {
        // I know this is bad but I don't see a better way to do it rn
        this.type = leakageType;
        if (leakageType == LeakageType.MultiTestLeakage) {
            // TODO set cause to new LeakageCause type
            this.cause = LeakageCause.RepeatDataEvaluation;
            this.taints = new ArrayList<>();
            this.lineNumbers = new ArrayList<>();
        } else {
            this.taints = setTaints(leakageType).stream().distinct().toList();
            this.cause = setCause();
            this.lineNumbers = setLineNumbers();
        }
    }

    public LeakageCause getCause() {
        return cause;
    }

    private LeakageCause setCause() {
        //TODO: revise
        if (taints.stream().anyMatch(taint -> taint.getPyCallExpression().toLowerCase().contains("vector"))) {
            return LeakageCause.VectorizingTextData;
        } else if (taints.stream().allMatch(taint -> taint.getPyCallExpression().toLowerCase().contains("split") || taint.getPyCallExpression().toLowerCase().contains("sample"))) {
            return LeakageCause.SplitBeforeSample;
        } else if (taints.stream().allMatch(taint -> taint.getPyCallExpression().toLowerCase().contains("flow"))) {
            return LeakageCause.DataAugmentation;
        }

        if (type.equals(LeakageType.PreprocessingLeakage)) {
            return LeakageCause.unknownPreprocessing;

        } else if (type.equals(LeakageType.OverlapLeakage)) {
            return LeakageCause.unknownOverlap;
        } else {
            return LeakageCause.unknown;
        }
    }

    private List<Integer> setLineNumbers() {
        return taints.stream().map(taint ->
                Utils.getActualLineNumberFromInternalLineNumber(LeakageResult.getFolderPath(),
                        Invocation.getInternalLineNumberFromInvocation(LeakageResult.getFolderPath(),
                                taint.getInvocation()))

        ).collect(Collectors.toList());


    }

    public List<Taint> getTaints() {
        return taints;
    }

    private List<Taint> setTaints(LeakageType leakageType) {
        List<Taint> taints;
        switch (leakageType) {
            case OverlapLeakage -> taints = TaintUtils.getTaintsFromFile(TaintLabel.dup).stream()
                    .map(taintString -> new Taint(taintString, TaintLabel.dup))
                    .collect(Collectors.toList());
            case PreprocessingLeakage -> taints = TaintUtils.getTaintsFromFile(TaintLabel.rowset).stream()
                    .map(taintString -> new Taint(taintString, TaintLabel.rowset))
                    .collect(Collectors.toList());

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
        return this.getTaints().stream().filter(
                taint -> taint.getPyCallExpression().equalsIgnoreCase(text) //equalsIgnoreCase MUST be used here
                //TODO: the pycall expression in the leakage analysis tool does not match what is actually in the test file
        ).findFirst().orElse(new Taint("", TaintLabel.dup));//TODO: better error handling
    }

    public void setLineNumber(int oldLineNumber, int newLineNumber) {
        for (int i = 0; i < this.lineNumbers.size(); i++) {
            if (this.lineNumbers.get(i) == oldLineNumber) {
                lineNumbers.set(i, newLineNumber);
            }
        }
    }

    public void removeLineNumbers(List<Integer> lineNumbersToRemove) {
        this.lineNumbers =
                lineNumbers.stream().filter(lineNumber -> !lineNumbersToRemove.contains(lineNumber)).toList();
    }

    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }


    public void addTaint(String lineContent) {
        Taint newTaint = new Taint(new Invocation("$invo4"), lineContent);
        this.taints.add(newTaint);
    }
}
