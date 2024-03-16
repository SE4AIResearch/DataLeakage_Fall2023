package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.util.HashMap;

public class LeakageTypeMapFactory {
   public static HashMap<LeakageType, String> getLeakageTypeMap() {
      HashMap<LeakageType, String> map = new HashMap<>();

      map.put(LeakageType.OverlapLeakage, "Overlap Leakage");
      map.put(LeakageType.PreprocessingLeakage, "Preprocessing Leakage");
      map.put(LeakageType.MultiTestLeakage, "Multi-Test Leakage");

      return map;
   }
}
