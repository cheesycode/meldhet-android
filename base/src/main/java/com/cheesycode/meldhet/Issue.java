package com.cheesycode.meldhet;

public class Issue {
    double lat;
    double lon;
    String tag;
    String id;

    public Issue(double lat, double lon, String tag, String id) {
        this.lat = lat;
        this.lon = lon;
        this.tag = tag;
        this.id = id;
    }
}
