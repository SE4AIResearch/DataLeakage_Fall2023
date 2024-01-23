package com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.leakage_sources;

import com.github.cd721.data_leakage_plugin.data.LeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageSourceKeyword;
import com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.LeakageInspection;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.InstanceElementVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources.SourceElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SourceInspection<T extends LeakageInstance, U extends LeakageSourceKeyword> extends LeakageInspection<T> {
    public abstract SourceElementVisitor<T,U> sourceElementVisitor(List<T> leakageInstances, @NotNull ProblemsHolder holder);

    @Override
    public PyElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {

        var leakageInstances = getLeakageInstancesForType(leakageAnalysisParser.LeakageInstances());
        return  sourceElementVisitor(leakageInstances, holder);
    }

}
