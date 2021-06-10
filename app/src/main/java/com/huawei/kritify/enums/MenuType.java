package com.huawei.kritify.enums;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public abstract class MenuType {
    @Retention(SOURCE)
    @StringDef({
            ALL,
            HOTELS,
            FOOD,
            CLOTHING
    })
    public @interface MenuTypeService {}
    public static final String ALL = "All";
    public static final String HOTELS = "Hotels";
    public static final String FOOD = "Food";
    public static final String CLOTHING = "Clothing";

    public abstract Object getMenuTypeService(@MenuTypeService String name);

}
