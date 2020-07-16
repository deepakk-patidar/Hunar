
package com.ics.hunar.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoStatusResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<VideoStatus> videoStatusList = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<VideoStatus> getVideoStatusList() {
        return videoStatusList;
    }

    public void setVideoStatusList(List<VideoStatus> videoStatusList) {
        this.videoStatusList = videoStatusList;
    }
}
