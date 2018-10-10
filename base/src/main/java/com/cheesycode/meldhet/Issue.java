package com.cheesycode.meldhet;

public class Issue {
    double lat;
    double lon;
    String tag;
    String id;
    String status;

    public Issue(double lat, double lon, String tag, String id, String status) {
        this.lat = lat;
        this.lon = lon;
        this.tag = tag;
        this.id = id;
        this.status = status;
    }
}
