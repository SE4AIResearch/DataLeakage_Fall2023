package com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.leakage_sources;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.OverlapLeakageSourceKeyword;
import com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.LeakageInspection;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.OverlapLeakageInstanceVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources.OverlapLeakageSourceVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources.SourceElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OverlapLeakageSourceInspection extends SourceInspection<OverlapLeakageInstance, OverlapLeakageSourceKeyword> {
    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }


    @Override
    public SourceElementVisitor<OverlapLeakageInstance, OverlapLeakageSourceKeyword> sourceElementVisitor(List<OverlapLeakageInstance> leakageInstances, @NotNull ProblemsHolder holder) {
        return new OverlapLeakageSourceVisitor(leakageInstances, holder);
    }
}
