package com.huawei.kritify.enums;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public abstract class EntityType {
    @Retention(SOURCE)
    @StringDef({
            HOTEL,
            RESTAURANT,
            CLOTHING_STORE
    })
    public @interface EntityTypeService {}
    public static final String HOTEL = "HOTEL";
    public static final String RESTAURANT = "RESTAURANT";
    public static final String CLOTHING_STORE = "CLOTHING_STORE";

    public abstract Object getEntityTypeService(@EntityTypeService String name);

}
