package com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.leakage_instances;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.LeakageInspection;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.InstanceElementVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.OverlapLeakageInstanceVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class InstanceInspection<T extends LeakageInstance> extends LeakageInspection<T> {

    public abstract InstanceElementVisitor<T> instanceElementVisitor(List<T> leakageInstances, @NotNull ProblemsHolder holder);

    @Override
    public LeakageType getLeakageType() {
        return null;
    }

    @Override
    public PyElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {
        var leakageInstances = getLeakageInstancesForType(leakageAnalysisParser.LeakageInstances());
        return instanceElementVisitor(leakageInstances, holder);
    }
}
