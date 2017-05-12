package com.getintent.crawler;

/**
 * Created by arkadiy on 12/05/17.
 */
public class WordCount {
    private final String word;
    private final long count;

    public WordCount(String word, long count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public long getCount() {
        return count;
    }
}
