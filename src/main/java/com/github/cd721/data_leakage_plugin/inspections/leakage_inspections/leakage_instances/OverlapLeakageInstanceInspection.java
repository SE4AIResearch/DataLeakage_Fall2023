package com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.leakage_instances;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.LeakageInspection;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.OverlapLeakageInstanceVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

public class OverlapLeakageInstanceInspection extends LeakageInspection<OverlapLeakageInstance> {

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }

    @Override
    public PyElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {
        var leakageInstances = leakageAnalysisParser.LeakageInstances();

        var overlapLeakageInstances = getLeakageInstancesForType(leakageInstances);
        return new OverlapLeakageInstanceVisitor(overlapLeakageInstances, holder);
    }


}
