package com.example.casiptv;

public class ChannelModel {
    private String name;
    private int streamId;
    private String streamIcon;

    public ChannelModel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStreamId() {
        return streamId;
    }

    public void setStreamId(int streamId) {
        this.streamId = streamId;
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
}
