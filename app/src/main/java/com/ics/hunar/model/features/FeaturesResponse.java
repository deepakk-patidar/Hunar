
package com.ics.hunar.model.features;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeaturesResponse {

    @SerializedName("trending_1")
    @Expose
    private String trending1;
    @SerializedName("trending_2")
    @Expose
    private String trending2;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;
    @SerializedName("subcategories")
    @Expose
    private List<Subcategory> subcategories = null;

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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

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

}
