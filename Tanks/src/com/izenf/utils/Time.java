package com.izenf.utils;

/**
 * Created by izenf on 27.03.2016.
 */
public class Time {
    public static final long SECOND = 1000000000l;

    public static long get(){
        return System.nanoTime();
    }

}
