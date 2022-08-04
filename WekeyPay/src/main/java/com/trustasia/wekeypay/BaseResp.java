package com.trustasia.wekeypay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseResp<T> implements Serializable {
    @SerializedName("error")
    public String error;
    @SerializedName("code")
    public int code;
    @SerializedName("data")
    public T data;

    public boolean isOk() {
        return code == 0;
    }
}
