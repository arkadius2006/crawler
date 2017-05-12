package com.getintent.crawler;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by arkadiy on 12/05/17.
 *
 * Implementation note. Dijkstra's algorithm.
 *
 * At every moment is time page graph contains three kinds of nodes/pages:
 * white - this page is identified, but not yet processed
 * grey - this page is being processed by some worker
 * black - this page is processed, meaning it has been word-counted and its links has been identified.
 *
 * Initialy graph contains only start page iwth level=0.
 * Each worker take white page from graph (it immeidately becomes grey), processes it and submits result back to graph
 * (meaning this page word counts added to statistics, this page becomes black, its links has been added to graph as
 * white pages with increased by one level). If no white pages available it the moment, worker waits. If graph is done (meaning
 * all nodes are black), worker gracefully dies.
 *
 * To avoid re-visiting the same pages that has been already procesed, graph keeps track of both white, grey and black nodes.
 * When submitting just found links only new pages are added to white page list.
 */
public class PageGraph {
    private final static Logger logger = Logger.getLogger(PageGraph.class);

    private final String startUrl;
    private final int searchDepth;
    private final int topLimit;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition someWhitePagesExistOrGraphIsDone = lock.newCondition();

    private final LinkedList<Page> whitePageList = new LinkedList<>();
    private final Map<String,Page> whitePageMap = new HashMap<>();
    private final Map<String, Page> greyPages = new HashMap<>();
    private final Map<String,Page> blackPages = new HashMap<>();

    private final WordCountSummator summator;

    public PageGraph(String startUrl, int searchDepth, int topLimit) { // todo move to summator
        this.startUrl = startUrl;
        this.searchDepth = searchDepth;
        this.topLimit = topLimit;

        this.summator = new WordCountSummator(topLimit);

        addStartPage();
    }

    private void addStartPage() {
        lock.lock();
        try {
            addWhitePage(new Page(startUrl, 0));
            someWhitePagesExistOrGraphIsDone.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public WhitePageRequestResult takeWhitePage() throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                if (hasWhitePage()) {
                    Page page = getWhitePage();
                    addGreyPage(page);
                    logger.info("Page grabbed " + page + ", whites: " + whitePageList.size());
                    return WhitePageRequestResult.whitePageGrabbed(page);
                } else if (graphIsDone()) {
                    return WhitePageRequestResult.graphIsDone();
                } else {
                    someWhitePagesExistOrGraphIsDone.await();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean hasWhitePage() {
        return !whitePageList.isEmpty();
    }

    private Page getWhitePage() {
        Page page = whitePageList.removeFirst();
        whitePageMap.remove(page.getUrl());
        return page;
    }

    private void addGreyPage(Page page) {
        greyPages.put(page.getUrl(), page);
    }

    private boolean graphIsDone() {
        return whitePageList.isEmpty() && greyPages.isEmpty();
    }

    public void submitPageResult(Page page, List<String> internalLinks, List<WordCount> wordCounts) {
        lock.lock();
        try {
            logger.info("Submitting page result: " + page.getUrl());

            if (isPageGrey(page)) {
                removePageFromGreyList(page);
                addPageToBlackList(page);

                if (page.getLevel() < searchDepth) {
                    for (String link : internalLinks) {
                        if (!pageIsVisited(link)) {
                            Page linkPage = new Page(link, page.getLevel() + 1);
                            addWhitePage(linkPage);
                        }
                    }
                }

                summator.add(wordCounts);

                someWhitePagesExistOrGraphIsDone.signalAll();
            } else {
                logger.warn("Ignore page result for " + page.getLevel());
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean pageIsVisited(String link) {
        if (blackPages.containsKey(link)) {
            return true;
        }

        if (greyPages.containsKey(link)) {
            return true;
        }

        if (whitePageMap.containsKey(link)) {
            return true;
        }

        return false;
    }

    private void addPageToBlackList(Page page) {
        blackPages.put(page.getUrl(), page);
    }

    private void removePageFromGreyList(Page page) {
        greyPages.remove(page.getUrl());
    }

    private boolean isPageGrey(Page page) {
        return greyPages.containsKey(page.getUrl());
    }

    private void addWhitePage(Page page) {
        whitePageList.addLast(page);
        whitePageMap.put(page.getUrl(), page);

        logger.info("Added page " + page + ", whites: " + whitePageList.size());
    }

    public void awaitGraphIsDone() throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                if (graphIsDone()) {
                    logger.info("Graph is done, blacks: " + blackPages.size());
                    return;
                } else {
                    someWhitePagesExistOrGraphIsDone.await();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public List<WordCount> getTopWordCounts() {
        lock.lock();
        try {
            return summator.getTopWordCounts();
        } finally {
            lock.unlock();
        }
    }
}
