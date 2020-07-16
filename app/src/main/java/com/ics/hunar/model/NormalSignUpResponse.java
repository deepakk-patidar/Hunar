
package com.ics.hunar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NormalSignUpResponse {

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private NormalSignUp normalSignUp;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NormalSignUp getNormalSignUp() {
        return normalSignUp;
    }

    public void setNormalSignUp(NormalSignUp normalSignUp) {
        this.normalSignUp = normalSignUp;
    }
}
