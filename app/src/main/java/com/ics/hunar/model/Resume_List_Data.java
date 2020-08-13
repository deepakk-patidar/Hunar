package com.ics.hunar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Resume_List_Data {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("video_id")
    @Expose
    private String video_id;

    @SerializedName("palying_time")
    @Expose
    private String palying_time;

    @SerializedName("total_time")
    @Expose
    private String total_time;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("subcategory")
    @Expose
    private String subcategory;

    @SerializedName("language_id")
    @Expose
    private String language_id;

    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    @SerializedName("video_name")
    @Expose
    private String video_name;

    @SerializedName("category_name")
    @Expose
    private String category_name;

    @SerializedName("subcategory_name")
    @Expose
    private String subcategory_name;

    @SerializedName("language")
    @Expose
    private String language;

    @SerializedName("video")
    @Expose
    private String video;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getPalying_time() {
        return palying_time;
    }

    public void setPalying_time(String palying_time) {
        this.palying_time = palying_time;
    }

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
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

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
