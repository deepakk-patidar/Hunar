
package com.ics.hunar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NormalLoginResponse {

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("data")
    @Expose
    private NormalLogin normalLogin;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public NormalLogin getNormalLogin() {
        return normalLogin;
    }

    public void setNormalLogin(NormalLogin normalLogin) {
        this.normalLogin = normalLogin;
    }
}
