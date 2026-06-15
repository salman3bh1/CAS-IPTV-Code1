package com.example.casiptv;

public class MovieModel {
    private String name;
    private String streamIcon;
    private int streamId;

    public MovieModel() {}

    public MovieModel(String name, String streamIcon) {
        this.name = name;
        this.streamIcon = streamIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreamIcon() {
        return streamIcon;
    }

    public void setStreamIcon(String streamIcon) {
        this.streamIcon = streamIcon;
    }

    public String getIcon() {
        return streamIcon;
    }

    public void setIcon(String icon) {
        this.streamIcon = icon;
    }

    public int getStreamId() {
        return streamId;
    }

    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }
}
