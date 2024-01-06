package com.github.cd721.data_leakage_plugin.inspections;

import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

public abstract class ElementVisitor extends PyElementVisitor {

    @Override
    public abstract void visitPyReferenceExpression(@NotNull PyReferenceExpression node) ;

    @Override
    public abstract void visitPyCallExpression(@NotNull PyCallExpression node);
}
