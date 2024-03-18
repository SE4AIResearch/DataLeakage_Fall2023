package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import org.jetbrains.annotations.NotNull;

public interface MultiTestData {
    String getCtx1();

    String getMeth();

    String getInvo();
    String getTest();


    boolean equals(@NotNull MultiTestData o);

}
