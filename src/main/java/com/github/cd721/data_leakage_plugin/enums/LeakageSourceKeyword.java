package com.github.cd721.data_leakage_plugin.enums;

import java.util.List;

public interface LeakageSourceKeyword {
    String getTaintKeyword();

    List<LeakageCause> getPotentialCauses();
    
    String toString();
}
