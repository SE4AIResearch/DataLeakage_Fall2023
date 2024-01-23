package com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.leakage_sources;

import com.github.cd721.data_leakage_plugin.data.PreprocessingLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.PreprocessingLeakageSourceKeyword;
import com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.LeakageInspection;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.InstanceElementVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.PreprocessingLeakageInstanceVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources.PreprocessingLeakageSourceVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_sources.SourceElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PreprocessingLeakageSourceInspection extends SourceInspection<PreprocessingLeakageInstance, PreprocessingLeakageSourceKeyword> {

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.PreprocessingLeakage     ;
    }

    @Override
    public SourceElementVisitor<PreprocessingLeakageInstance, PreprocessingLeakageSourceKeyword> sourceElementVisitor(List<PreprocessingLeakageInstance> leakageInstances, @NotNull ProblemsHolder holder) {
        return new PreprocessingLeakageSourceVisitor(leakageInstances, holder);

    }


}
