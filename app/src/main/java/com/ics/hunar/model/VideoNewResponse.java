
package com.ics.hunar.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoNewResponse {

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("data")
    @Expose
    private List<VideoNew> videoNewList = null;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<VideoNew> getVideoNewList() {
        return videoNewList;
    }

    public void setVideoNewList(List<VideoNew> videoNewList) {
        this.videoNewList = videoNewList;
    }
}
