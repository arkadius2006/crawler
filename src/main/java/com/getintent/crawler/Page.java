package com.getintent.crawler;

/**
 * Created by arkadiy on 12/05/17.
 */
public class Page {
    private final String url;
    private final int level;

    public Page(String url, int level) {
        this.url = url;
        this.level = level;
    }

    public String getUrl() {
        return url;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "{url='" + url + "', level=" + level + "}";
    }
}
