
package com.ics.hunar.model.searching;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Searching {

    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;
    @SerializedName("subcategories")
    @Expose
    private List<Subcategory> subcategories = null;
    @SerializedName("videos")
    @Expose
    private List<Video> videos = null;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

}
