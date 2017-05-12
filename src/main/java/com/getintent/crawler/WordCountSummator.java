package com.getintent.crawler;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by arkadiy on 12/05/17.
 */
public class WordCountSummator {
    final private int topLimit;
    final private Map<String, Long> wordCountMap = new HashMap<>();

    public WordCountSummator(int topLimit) {
        this.topLimit = topLimit;
    }

    public void add(List<WordCount> newWordCounts) {
        newWordCounts.forEach(new Consumer<WordCount>() {
            @Override
            public void accept(WordCount wordCount) {
                add(wordCount);
            }
        });
    }

    private void add(WordCount wc) {
        long currentValue = wordCountMap.getOrDefault(wc.getWord(), 0L); // todo this is not efficient, can be done in one operation
        long newValue = currentValue + wc.getCount();
        wordCountMap.put(wc.getWord(), newValue);
    }

    public List<WordCount> getTopWordCounts() {
        // todo this is VERY inefficient way to find top-100, it is n lon n complexity, we can do we n complexity
        List<WordCount> allItems = getWordCountList();
        allItems.sort(new Comparator<WordCount>() {
            @Override
            public int compare(WordCount o1, WordCount o2) {
                return Long.compare(o2.getCount(), o1.getCount());
            }
        });

        return new ArrayList<>(allItems.subList(0, Integer.min(allItems.size(), topLimit)));
    }

    private List<WordCount> getWordCountList() {
        List<WordCount> list = new ArrayList<>();
        wordCountMap.forEach(new BiConsumer<String, Long>() {
            @Override
            public void accept(String s, Long aLong) {
                list.add(new WordCount(s, aLong));
            }
        });

        return list;
    }

}
