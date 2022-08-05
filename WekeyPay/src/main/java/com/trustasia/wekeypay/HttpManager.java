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
    private final Gson gson = new Gson();


    private HttpManager() {
    }

    public void init(PaymentManager.TokenFetcher fetcher) {
        this.mFetcher = fetcher;
        this.mClient = new OkHttpClient.Builder().build();
    }

    public static synchronized HttpManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpManager();
        }
        return INSTANCE;
    }


    public void queryPaymentState(String token, PaymentManager.ResultCallback callback) {
        Call call = mClient.newCall(new Request.Builder().url("https://pay-dev.wekey.cn/ta-finance/subscribe/status").get().addHeader("Authorization", "Bearer " + token).addHeader("platform", "Android").build());
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
                    Log.d("TAG", json);
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

        Call call = mClient.newCall(new Request.Builder().url("https://pay-dev.wekey.cn/ta-finance/subscribe/submit").put(RequestBody.create("{\"pay_chan\":\"alipay\"}", MediaType.parse("application/json"))).addHeader("Authorization", "Bearer " + token).addHeader("platform", "Android").build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onResult(-1, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String json = Objects.requireNonNull(response.body()).string();
                    Log.d("TAG", json);
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

    public void testToken(String productId, PaymentManager.ResultCallback resultCallback) {
        Call call = mClient.newCall(new Request.Builder().url("https://temp.wekey.cn/subscribe").post(RequestBody.create("{\"product_id\":\"" + productId + "\"}", MediaType.parse("application/json"))).addHeader("platform", "Android").build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                resultCallback.onResult(-1, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String json = Objects.requireNonNull(response.body()).string();
                    Log.d("TAG", json);
                    BaseResp<SubscribeStatus> token = gson.fromJson(json, new TypeToken<BaseResp<SubscribeStatus>>() {
                    }.getType());
                    if (token.isOk()) {
                        resultCallback.onResult(0, token.data.token);
                    } else {
                        resultCallback.onResult(token.code, token.error);
                    }
                } catch (Exception e) {
                    resultCallback.onResult(-1, e.getMessage());
                }
            }
        });
    }

    void fetchToken(String productId, PaymentManager.ResultCallback callback) {
        mFetcher.getToken(productId, callback);
    }
}
