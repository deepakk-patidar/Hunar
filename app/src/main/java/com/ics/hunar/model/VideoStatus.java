
package com.ics.hunar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoStatus {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("video_id")
    @Expose
    private String videoId;
    @SerializedName("is_unlocked")
    @Expose
    private String isUnlocked;
    @SerializedName("is_played")
    @Expose
    private String isPlayed;
    @SerializedName("is_downloaded")
    @Expose
    private String isDownloaded;
    @SerializedName("is_favourite")
    @Expose
    private String isFavourite;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("subcategory")
    @Expose
    private String subcategory;
    @SerializedName("language_id")
    @Expose
    private String languageId;
    @SerializedName("video")
    @Expose
    private String video;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("video_name")
    @Expose
    private String videoName;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("row_order")
    @Expose
    private String rowOrder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getIsUnlocked() {
        return isUnlocked;
    }

    public void setIsUnlocked(String isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    public String getIsPlayed() {
        return isPlayed;
    }

    public void setIsPlayed(String isPlayed) {
        this.isPlayed = isPlayed;
    }

    public String getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(String isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public String getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getRowOrder() {
        return rowOrder;
    }

    public void setRowOrder(String rowOrder) {
        this.rowOrder = rowOrder;
    }

}
