package com.github.cd721.data_leakage_plugin.data.taints;

import com.github.cd721.data_leakage_plugin.data.Invocation;
import com.github.cd721.data_leakage_plugin.enums.TaintLabel;


/**
 * Very roughly speaking, taints correspond to sources of data leakage.
 */
public class Taint {

    private final Invocation invocation;
    private final String pyCallExpression;
    private final TaintLabel taintType;

    public Taint(String taintString, TaintLabel taintType) {
        this.taintType = taintType;
        this.invocation = getInvoFromTaint(taintString);
        this.pyCallExpression = getPyCallExpressionFromTaint(taintString);
    }

    private Invocation getInvoFromTaint(String taint) {
        String[] taintSplit = taint.split("\t");
        if (taintSplit.length < 6) {
            //TODO: how to handle this case?
            return new Invocation("$invo0");
        }

        return new Invocation(taintSplit[4]);
    }



    private String getPyCallExpressionFromTaint(String taint) {
        String[] taintSplit = taint.split("\t");
        if (taintSplit.length < 6) {
            return "";
        }

        return (taintSplit[5]);
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public String getPyCallExpression() {
        return pyCallExpression;
    }

}
