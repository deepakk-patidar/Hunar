
package com.ics.hunar.model.searching;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchingResponse {

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("data")
    @Expose
    private Searching searching;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Searching getSearching() {
        return searching;
    }

    public void setSearching(Searching searching) {
        this.searching = searching;
    }
}
