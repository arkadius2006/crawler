package com.getintent.crawler;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by arkadiy on 12/05/17.
 */
public class PageWorker { // todo should worker know about page level or only url, should worker collect links for final page????? optimization
    private final static Logger logger = Logger.getLogger(PageWorker.class);

    private final String name;
    private final PageGraph graph;
    private final Thread thread;

    public PageWorker(String name, PageGraph graph) {
        this.name = name;
        this.graph = graph;
        this.thread = new Thread(new PageWorkerRunnable(), this.name);
    }

    public void start() {
        logger.info("Starting worker " + name);
        thread.start();
    }

    private void processPage(Page page) {
        logger.info("Processing page: " + page.getUrl());
        PageInspectionResult pir = getPageInspectionResult(page);
        graph.submitPageResult(page, pir.getInternalLinks(), pir.getWordCounts());
    }

    private PageInspectionResult getPageInspectionResult(Page page) {
        try {
            return new PageInspector().inspect(page.getUrl());
        } catch (Exception e) {
            logger.error("Failed to process page " + page.getUrl(), e);

            // ignore word counts on this page
            return new PageInspectionResult(new ArrayList<>(), new ArrayList<>());
        }
    }

    private class PageWorkerRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                WhitePageRequestResult requestResult;
                try {
                    logger.info("Worker " + name + " requests for white page");
                    requestResult = graph.takeWhitePage();
                } catch (InterruptedException x) {
                    logger.error("Worker interrupted", x);
                    return;
                }

                switch (requestResult.getStatus()) {
                    case WHITE_PAGE_GRABBED:
                        Page page = requestResult.getPage();
                        processPage(page);
                        continue;
                    case GRAPH_IS_DONE:
                        logger.info("Graph is done, worker going to die");
                        return;
                    default:
                        // impossible
                        logger.error("Unexpected status: " + requestResult.getStatus());
                        return;
                }
            }
        }
    }
}
