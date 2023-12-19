package com.github.cd721.data_leakage_plugin.inspections;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class InspectionBundle extends DynamicBundle {
    public static final InspectionBundle INSTANCE = new InspectionBundle();

    @NotNull
    public static String get(@NotNull
                             @PropertyKey(resourceBundle = "messages.InspectionText")
                             String key,
                             Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }

    private InspectionBundle() {
        super("messages.InspectionText");
    }
}