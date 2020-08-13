package com.ics.hunar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Resume_List_Model {

    @SerializedName("error")
    @Expose
    private Boolean error;

    @SerializedName("data")
    @Expose
    private List<Resume_List_Data> data;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<Resume_List_Data> getData() {
        return data;
    }

    public void setData(List<Resume_List_Data> data) {
        this.data = data;
    }
}
