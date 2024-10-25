package com.ffsupver.asplor.util;

import java.util.Comparator;
import java.util.Map;

public class ComparatorByMap implements Comparator<String> {
    private final Map<String,Integer> order;
    public ComparatorByMap(Map<String,Integer> order){
        this.order = order;
    }
    @Override
    public int compare(String o1, String o2) {
        int value = order.get(o1) - order.get(o2);
        return Integer.compare(value, 0);
    }
}
