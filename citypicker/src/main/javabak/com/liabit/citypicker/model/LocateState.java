package com.liabit.citypicker.model;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

public class LocateState {
    public static final int LOCATING    = 123;
    public static final int SUCCESS     = 132;
    public static final int FAILURE     = 321;

    @IntDef({SUCCESS, FAILURE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State{}
}
