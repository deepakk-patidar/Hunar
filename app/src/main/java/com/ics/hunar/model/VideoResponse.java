
package com.ics.hunar.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoResponse {

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("data")
    @Expose
    private List<VideoA> VideoAList = null;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<VideoA> getVideoAList() {
        return VideoAList;
    }

    public void setVideoAList(List<VideoA> videoAList) {
        VideoAList = videoAList;
    }
}
