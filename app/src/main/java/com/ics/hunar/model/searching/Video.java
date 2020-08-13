
package com.ics.hunar.model.searching;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("id")
    @Expose
    private String id;
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
    @SerializedName("is_unlocked")
    @Expose
    private String is_unlocked;
    @SerializedName("is_played")
    @Expose
    private String is_played;
    @SerializedName("is_downloaded")
    @Expose
    private String is_downloaded;
    @SerializedName("is_favourite")
    @Expose
    private String is_favourite;

    public String getIs_unlocked() {
        return is_unlocked;
    }

    public void setIs_unlocked(String is_unlocked) {
        this.is_unlocked = is_unlocked;
    }

    public String getIs_played() {
        return is_played;
    }

    public void setIs_played(String is_played) {
        this.is_played = is_played;
    }

    public String getIs_downloaded() {
        return is_downloaded;
    }

    public void setIs_downloaded(String is_downloaded) {
        this.is_downloaded = is_downloaded;
    }

    public String getIs_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(String is_favourite) {
        this.is_favourite = is_favourite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
