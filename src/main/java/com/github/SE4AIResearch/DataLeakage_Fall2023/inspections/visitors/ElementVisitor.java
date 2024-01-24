package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyElementVisitor;

public abstract class ElementVisitor<T extends LeakageInstance, U extends LeakageSourceKeyword> extends PyElementVisitor {
    public ProblemsHolder holder;

    public abstract LeakageType getLeakageType();








}
