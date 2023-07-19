package com.shanjun.ncbi.uitls;

import java.util.HashMap;

public class MonthConverter {
    private static final HashMap<String, Integer> MONTHS = new HashMap<>();

    static {
        MONTHS.put("Jan", 1);
        MONTHS.put("Feb", 2);
        MONTHS.put("Mar", 3);
        MONTHS.put("Apr", 4);
        MONTHS.put("May", 5);
        MONTHS.put("Jun", 6);
        MONTHS.put("Jul", 7);
        MONTHS.put("Aug", 8);
        MONTHS.put("Sep", 9);
        MONTHS.put("Oct", 10);
        MONTHS.put("Nov", 11);
        MONTHS.put("Dec", 12);
    }

    public static int convert(String month) {
        return MONTHS.get(month);
    }
}
