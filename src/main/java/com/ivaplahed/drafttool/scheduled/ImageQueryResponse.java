package com.ivaplahed.drafttool.scheduled;

import java.util.Map;

public record ImageQueryResponse(Query query) {

    public record Query(Map<String, Page> pages) {}

    public record Page(int pageid, int ns, String title, String imagerepository, ImageInfo[] imageinfo) {

        public record ImageInfo(String url, String descriptionurl, String descriptionshorturl) {}
    }
}