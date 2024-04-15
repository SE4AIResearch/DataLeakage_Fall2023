package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.util.ArrayList;
import java.util.List;

public class MultiTestLeakageSource extends LeakageSource<LeakageType.MultitestLeakage> {
    private final List<Taint> taints;
    private List<Integer> lineNumbers;
    private final LeakageCause cause;
    public MultiTestLeakageSource() {
        this.cause = LeakageCause.RepeatDataEvaluation;
        this.taints = new ArrayList<>();
        this.lineNumbers = new ArrayList<>();
    }
   @Override
    public void setLineNumber(int oldLineNumber, int newLineNumber) {
        for (int i = 0; i < this.lineNumbers.size(); i++) {
            if (this.lineNumbers.get(i) == oldLineNumber) {
               this.lineNumbers.set(i, newLineNumber);
            }
        }
    }
    @Override
    public void removeLineNumbers(List<Integer> lineNumbersToRemove) {
        this.lineNumbers =
                lineNumbers.stream().filter(lineNumber -> !lineNumbersToRemove.contains(lineNumber)).toList();
    }
    @Override
    public List<LeakageCause> getPotentialCauses() {
        return List.of(LeakageCause.RepeatDataEvaluation);
    }

    @Override
    public List<Taint> getTaints() {
        return taints;
    }

    @Override
    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }
}
