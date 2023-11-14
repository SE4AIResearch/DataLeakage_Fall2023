package com.github.cd721.data_leakage_plugin;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class DataLeakageIndicatorFactory {
    private final List<DataLeakageIndicator> indicators;

    //TODO: fix improper use of factory pattern
    public DataLeakageIndicatorFactory() {
      indicators = new ArrayList<>();
        indicators.add(new OverlapLeakageIndicator());
        indicators.add(new MultitestLeakageIndicator());
        indicators.add(new PreprocessingLeakageIndicator());

    }

    public DataLeakageIndicator GetIndicatorForLeakageType(LeakageType leakageType) {
        return switch (leakageType) {
            case OverlapLeakage -> indicators.get(0);
            case MultiTestLeakage -> indicators.get(1);
            case PreprocessingLeakage -> indicators.get(2);
        };
    }
}
