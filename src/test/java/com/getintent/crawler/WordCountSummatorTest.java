package com.getintent.crawler;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arkadiy on 12/05/17.
 */
public class WordCountSummatorTest {

    @Test
    public void test() {
        WordCountSummator wordCountSummator = new WordCountSummator(1);

        List<WordCount> list = new ArrayList<>();
        list.add(new WordCount("a", 1L));
        list.add(new WordCount("b", 2L));

        wordCountSummator.add(list);

        List<WordCount> counts = wordCountSummator.getTopWordCounts();
        Assert.assertEquals(1, counts.size());
        WordCount topItem = counts.get(0);
        Assert.assertEquals("b", topItem.getWord());
    }
}
