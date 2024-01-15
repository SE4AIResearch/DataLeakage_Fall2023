package com.github.cd721.data_leakage_plugin.inspections.leakage_inspections;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.inspections.ElementVisitor;
import com.github.cd721.data_leakage_plugin.inspections.OverlapLeakageElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

public class OverlapLeakageInspection extends LeakageInspection<OverlapLeakageInstance> {

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }

    @Override
    public ElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {
        var leakageInstances = leakageAnalysisParser.LeakageInstances();

        var overlapLeakageInstances = getLeakageInstancesForType(leakageInstances);
        return new OverlapLeakageElementVisitor(overlapLeakageInstances, holder);
    }


}
