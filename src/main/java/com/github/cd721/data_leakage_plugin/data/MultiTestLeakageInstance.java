package com.github.cd721.data_leakage_plugin.data;

import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.jetbrains.python.psi.PyReferenceExpression;

public class MultiTestLeakageInstance extends LeakageInstance{

    private PyReferenceExpression test;
    public MultiTestLeakageInstance(int lineNumber, LeakageType type, Invocation invocation) {
        super(lineNumber, type, invocation);
    }
}
