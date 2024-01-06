package com.github.cd721.data_leakage_plugin.data.leakage_data;

import com.github.cd721.data_leakage_plugin.common_utils.Utils;
import com.github.cd721.data_leakage_plugin.data.Invocation;
import com.github.cd721.data_leakage_plugin.data.Taint;


/**
 * The source of particular instance of data leakage exists on a {@link #lineNumber} and is associated with
 * a {@link Taint}.
 */

public class LeakageSource {

    private final Taint taint;
    private final int lineNumber;

    public LeakageSource() {
        this.taint = new Taint();
        this.lineNumber = Utils.getActualLineNumberFromInternalLineNumber(LeakageOutput.folderPath(), Invocation.getInternalLineNumberFromInvocation(LeakageOutput.folderPath(), this.taint.getInvocation()));
    }


    public int getLineNumber() {
        return lineNumber;
    }
}
