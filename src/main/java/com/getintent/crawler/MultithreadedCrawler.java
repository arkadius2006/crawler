package com.getintent.crawler;

import org.apache.log4j.Logger;

import java.io.IOException;

import java.util.List;

/**
 * Created by arkadiy on 12/05/17.
 */
public class MultithreadedCrawler {
    private final static Logger logger = Logger.getLogger(MultithreadedCrawler.class);

    private final PageGraph graph;
    private final PageWorker[] workers;
    private final WordCountPrinter printer;

    public MultithreadedCrawler(String startUrl, int threadCount, int searchDepth, int topLimit) {
        this.graph = new PageGraph(startUrl, searchDepth, topLimit);
        this.workers = new PageWorker[threadCount];
        for (int i = 0; i < threadCount; i++) {
            this.workers[i]  = new PageWorker("Worker-" + i, this.graph);
        }
        this.printer = new WordCountPrinter();
    }

    public void start() throws IOException, InterruptedException {

        // start workers
        logger.info("Starting workers");
        for (PageWorker worker : workers) {
            worker.start();
        }

        // await graph is done
        logger.info("Awaiting graph is done");
        graph.awaitGraphIsDone();

        // retrieve result
        logger.info("Collect top word counts");
        List<WordCount> topWordCounts = graph.getTopWordCounts();

        // print it
        logger.info("Print result");
        printer.print(topWordCounts);
    }
}
