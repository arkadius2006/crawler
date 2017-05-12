package com.getintent.crawler;

/**
 * Created by arkadiy on 12/05/17.
 */
public class WhitePageRequestResult {
    private final WhitePageRequestStatus status;
    private final Page page;


    public static WhitePageRequestResult whitePageGrabbed(Page page) {
        if (page == null) {
            throw new NullPointerException("Page == null");
        }
        return new WhitePageRequestResult(WhitePageRequestStatus.WHITE_PAGE_GRABBED, page);
    }

    public static WhitePageRequestResult graphIsDone() {
        return new WhitePageRequestResult(WhitePageRequestStatus.GRAPH_IS_DONE, null);
    }

    private WhitePageRequestResult(WhitePageRequestStatus status, Page page) {
        this.status = status;
        this.page = page;
    }

    public WhitePageRequestStatus getStatus() {
        return status;
    }

    public Page getPage() {
        return page;
    }
}
