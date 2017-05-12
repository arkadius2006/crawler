package com.getintent.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created by arkadiy on 12/05/17.
 */
public class PageInspector {

    public PageInspectionResult inspect(String url) throws IOException {
        Document document = Jsoup.connect(url).get();


        List<String> links = getLinks(document, url);

        List<WordCount> wordCounts = getWordCounts(document);

        return new PageInspectionResult(links, wordCounts);
    }

    private List<String> getLinks(Document document, String baseUrl) throws MalformedURLException {
        List<String> links = new ArrayList<>();
        Elements elements = document.select("a[href]");

        for (Element e : elements) {
            String ref = e.attr("href");

            if (isRelativeLink(ref)) {
                links.add(resolveRelativeLink(ref, baseUrl));
            }

        }

        return links;
    }

    private List<WordCount> getWordCounts(Document document) {
        Map<String, Long> map = new HashMap<>();
        for (String word : getWords(document)) {
            updateCount(map, word);
        }

        List<WordCount> list = new ArrayList<>();
        map.forEach(new BiConsumer<String, Long>() {
            @Override
            public void accept(String s, Long aLong) {
                list.add(new WordCount(s, aLong));
            }
        });
        return list;
    }

    private void updateCount(Map<String, Long> counts, String word) {
        // todo can be optimized into one map operation
        Long current  = counts.getOrDefault(word, 0L);
        counts.put(word, current + 1);
    }

    private List<String> getWords(Document document) {
        List<String> words = new ArrayList<String>();
        for (String text : getText(document)) {
            words.addAll(tokenize(text));
        }

        return words;
    }

    private List<String> tokenize(String text) {
        if (text != null && !text.isEmpty()) {
            String[] wordsArray = text.split("\\W+");

            List<String> wordList = new ArrayList<>();
            for (String word : wordsArray) {
                if (word != null && !word.isEmpty()) {
                    wordList.add(word);
                }
            }
            return wordList;
        } else {
           return new ArrayList<String>();
        }
    }

    private List<String> getText(Document document) {
        List<String> texts = new ArrayList<String>();

        document.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int i) {
                if (node instanceof Element) {
                    Element e = (Element) node;
                    String text = e.ownText();
                    texts.add(text);
                }
            }

            @Override
            public void tail(Node node, int i) {
                // ignore
            }
        });

        return texts;
    }


    private boolean isRelativeLink(String link) {
        return link.startsWith("/");
    }

    private String resolveRelativeLink(String link, String baseUrl) throws MalformedURLException {
        URL url = new URL(baseUrl);
        return url.getProtocol() + "://" + url.getAuthority() + link;
    }

}
