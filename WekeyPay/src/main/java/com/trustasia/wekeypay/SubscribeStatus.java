package com.trustasia.wekeypay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubscribeStatus implements Serializable {

    @SerializedName("token")
    public String token;
    @SerializedName("status")
    public String status;
    @SerializedName("result")
    public String result;

}
