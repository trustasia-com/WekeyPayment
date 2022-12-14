package com.trustasia.wekeypay;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpManager {
    private static HttpManager INSTANCE;
    private PaymentManager.TokenFetcher mFetcher;
    private OkHttpClient mClient;
    private String baseUrl;
    private final Gson gson = new Gson();
    private boolean isDebug = false;


    private HttpManager() {
    }

    public void init(String baseUrl, PaymentManager.TokenFetcher fetcher) {
        this.mFetcher = fetcher;
        this.baseUrl = baseUrl;
        this.mClient = new OkHttpClient.Builder().build();
    }

    public void debuggable() {
        isDebug = true;
    }

    public static synchronized HttpManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpManager();
        }
        return INSTANCE;
    }

    private void log(String msg) {
        if (isDebug) Log.d("WekeyPay", msg);
    }


    public void queryPaymentState(String token, PaymentManager.ResultCallback callback) {
        String url = baseUrl + "/ta-finance/subscribe/status";
        log("Request Url --> " + url);
        Call call = mClient.newCall(new Request.Builder().url(url).get().addHeader("Authorization", "Bearer " + token).addHeader("platform", "Android").build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onResult(-1, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String json = Objects.requireNonNull(response.body()).string();
                    log("Response --> \n" + json);
                    BaseResp<SubscribeStatus> token = gson.fromJson(json, new TypeToken<BaseResp<SubscribeStatus>>() {
                    }.getType());
                    if (token.isOk()) {
                        callback.onResult(0, token.data.status);
                    } else {
                        callback.onResult(token.code, token.error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onResult(-1, e.getMessage());
                }
            }
        });
    }

    public void startPayment(String token, PaymentManager.ResultCallback callback) {
        String url = baseUrl + "/ta-finance/subscribe/submit";
        log("Request Url --> " + url);
        Call call = mClient.newCall(new Request.Builder().url(url).put(RequestBody.create("{\"pay_chan\":\"alipay\"}", MediaType.parse("application/json"))).addHeader("Authorization", "Bearer " + token).addHeader("platform", "Android").build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onResult(-1, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String json = Objects.requireNonNull(response.body()).string();
                    log("Response --> \n" + json);
                    BaseResp<SubscribeStatus> token = gson.fromJson(json, new TypeToken<BaseResp<SubscribeStatus>>() {
                    }.getType());
                    if (token.isOk()) {
                        callback.onResult(0, token.data.result);
                    } else {
                        callback.onResult(token.code, token.error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onResult(-1, e.getMessage());
                }
            }
        });
    }


    void fetchToken(String productId, PaymentManager.ResultCallback callback) {
        mFetcher.getToken(productId, callback);
    }
}
