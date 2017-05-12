package com.getintent.crawler;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Created by arkadiy on 12/05/17.
 */
public class Launcher {
    private final static Logger logger = Logger.getLogger(Launcher.class);

    private final static String START_URL_KEY = "crawler.start-url";
    private final static String THREAD_COUNT_KEY = "crawler.thread-count";
    private final static String SEARCH_DEPTH_KEY = "crawler.search-depth";
    private final static String TOP_LIMIT_KEY = "crawler.top-limit";

    public static void main(String[] args) throws Exception {
        Properties applicationProperties = getApplicationProperties(args);
        MultithreadedCrawler crawler = createCrawler(applicationProperties);
        startCrawler(crawler);
    }

    private static Properties getApplicationProperties(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Missing arg (expected 1 arg)");
        } else if (args.length  > 1) {
            throw new IllegalArgumentException("Too many args (expected 1 arg)");
        }

        String applicationPropertiesPath = args[0];

        return getApplicationProperties(applicationPropertiesPath);
    }

    private static Properties getApplicationProperties(String path) throws IOException {
        logger.info("Loading application properties from path '" + path + "'");
        Properties properties = new Properties();
        File file = new File(path);
        logger.info("Absolute application properties path is: " + file.getAbsolutePath());
        InputStream is = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        Reader reader  = new InputStreamReader(bis);
        properties.load(reader);
        reader.close();
        return properties;
    }


    private static MultithreadedCrawler createCrawler(Properties properties) {
        logger.info("Parsing application properties and creating crawler instance");
        String startUrl = PropertiesHelper.getUrl(properties, START_URL_KEY);
        int threadCount = PropertiesHelper.getPositiveInt(properties, THREAD_COUNT_KEY);
        int searchDepth = PropertiesHelper.getNonNegativeInt(properties, SEARCH_DEPTH_KEY);
        int topLimit = PropertiesHelper.getPositiveInt(properties, TOP_LIMIT_KEY);
        return new MultithreadedCrawler(startUrl, threadCount, searchDepth, topLimit);
    }

    private static void startCrawler(MultithreadedCrawler crawler) throws IOException, InterruptedException {
        logger.info("Starting crawler");
        crawler.start();
    }

}
