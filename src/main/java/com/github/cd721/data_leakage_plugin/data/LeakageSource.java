package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.common_utils.Utils;
import com.github.cd721.data_leakage_plugin.data.taints.Taint;
import com.github.cd721.data_leakage_plugin.data.taints.TaintUtils;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.TaintLabel;

import java.util.List;
import java.util.stream.Collectors;


/**
 * The source of particular instance of data leakage exists on multiple {@link #lineNumbers} and is associated with
 * a {@link #taints}.
 */

public class LeakageSource {

    private final List<Taint> taints;
    private final List<Integer> lineNumbers;

    public LeakageSource(LeakageType leakageType) {
        this.taints = setTaints(leakageType);
        this.lineNumbers = setLineNumbers();


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


    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }
}
