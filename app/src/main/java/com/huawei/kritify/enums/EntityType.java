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
    public static final String HOTEL = "Hotel";
    public static final String RESTAURANT = "Restaurant";
    public static final String CLOTHING_STORE = "Clothing Store";

    public abstract Object getEntityTypeService(@EntityTypeService String name);

}
