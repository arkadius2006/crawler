package com.getintent.crawler;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by arkadiy on 12/05/17.
 */
public class WordCountPrinter {
    private final static Logger logger = Logger.getLogger(WordCountPrinter.class);


    public void print(List<WordCount> items) {
        int i = 0;
        for (WordCount item : items) {
            logger.info("#" + i + ": '" + item.getWord() + "' -> " + item.getCount());
            ++i;
        }
    }
}
