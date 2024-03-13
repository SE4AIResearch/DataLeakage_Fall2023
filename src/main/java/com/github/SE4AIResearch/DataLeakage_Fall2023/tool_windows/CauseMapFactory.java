package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;

import java.util.HashMap;

public class CauseMapFactory {
   public static HashMap<LeakageCause, String> getCauseMap() {
      HashMap<LeakageCause, String> map = new HashMap<>();

      map.put(LeakageCause.SplitBeforeSample, "Data split before sampling.");
      map.put(LeakageCause.DataAugmentation, "Use caution when performing data augmentation.");
      map.put(LeakageCause.VectorizingTextData, "Vectorizer fit on train and test data together.");
      map.put(LeakageCause.RepeatDataEvaluation, "Same data used for multiple evalutations");
      map.put(LeakageCause.unknown, "Unknown");

      return map;
   }
}
