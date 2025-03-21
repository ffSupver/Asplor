package com.ffsupver.asplor.util;

import com.ffsupver.asplor.screen.guideBook.GuideBookScreen;

import java.util.Comparator;
import java.util.Map;

public class ComparatorByMap implements Comparator<String> {
    private final Map<GuideBookScreen.ChapterData,Integer> order;
    public ComparatorByMap(Map<GuideBookScreen.ChapterData,Integer> order){
        this.order = order;
    }
    @Override
    public int compare(String o1, String o2) {
        int order1 = 0;
        int order2 = 0;
        for (Map.Entry<GuideBookScreen.ChapterData,Integer> chapterData : order.entrySet()){
            if (chapterData.getKey().name.equals(o1)){
                order1 = chapterData.getValue();
            }
            if (chapterData.getKey().name.equals(o2)){
                order2 = chapterData.getValue();
            }
        }
        int value = order1 - order2;
        return Integer.compare(value, 0);
    }
}