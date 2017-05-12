package com.getintent.crawler;

import java.util.List;

/**
 * Created by arkadiy on 12/05/17.
 */
public class PageInspectionResult {
    private final List<String> internalLinks;
    private final List<WordCount> wordCounts;

    public PageInspectionResult(List<String> internalLinks, List<WordCount> wordCounts) {
        this.internalLinks = internalLinks;
        this.wordCounts = wordCounts;
    }

    public List<String> getInternalLinks() {
        return internalLinks;
    }

    public List<WordCount> getWordCounts() {
        return wordCounts;
    }
}
