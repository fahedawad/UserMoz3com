package com.example.usermoz3com.ViewPagerClass;

public class Pager {
    String id;
    int uri;

    public Pager() {
    }

    public Pager(String id, int uri) {
        this.id = id;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUri() {
        return uri;
    }

    public void setUri(int uri) {
        this.uri = uri;
    }
}
