
package com.ics.hunar.model.features;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subcategory {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("maincat_id")
    @Expose
    private String maincatId;
    @SerializedName("language_id")
    @Expose
    private String languageId;
    @SerializedName("subcategory_name")
    @Expose
    private String subcategoryName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("featured")
    @Expose
    private String featured;
    @SerializedName("trending_1")
    @Expose
    private String trending1;
    @SerializedName("trending_2")
    @Expose
    private String trending2;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("row_order")
    @Expose
    private String rowOrder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaincatId() {
        return maincatId;
    }

    public void setMaincatId(String maincatId) {
        this.maincatId = maincatId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public String getTrending1() {
        return trending1;
    }

    public void setTrending1(String trending1) {
        this.trending1 = trending1;
    }

    public String getTrending2() {
        return trending2;
    }

    public void setTrending2(String trending2) {
        this.trending2 = trending2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRowOrder() {
        return rowOrder;
    }

    public void setRowOrder(String rowOrder) {
        this.rowOrder = rowOrder;
    }

}
