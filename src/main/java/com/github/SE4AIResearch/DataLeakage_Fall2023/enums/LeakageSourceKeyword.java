package com.github.SE4AIResearch.DataLeakage_Fall2023.enums;

import java.util.List;

public interface LeakageSourceKeyword {
    String getTaintKeyword();

    List<LeakageCause> getPotentialCauses();
    
    String toString();
}
