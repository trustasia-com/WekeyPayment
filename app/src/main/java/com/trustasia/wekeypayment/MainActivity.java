package com.trustasia.wekeypayment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trustasia.wekeypay.BaseResp;
import com.trustasia.wekeypay.PaymentManager;
import com.trustasia.wekeypay.SubscribeStatus;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_payment_lv_1).setOnClickListener(this);
        findViewById(R.id.btn_payment_lv_2).setOnClickListener(this);
        findViewById(R.id.btn_payment_lv_3).setOnClickListener(this);
        PaymentManager.getInstance().init("https://pay-dev.wekey.cn", this::testTokenFetch);
        PaymentManager.debuggable();
    }

    @Override
    public void onClick(View view) {
        if (R.id.btn_payment_lv_1 == view.getId()) {
            PaymentManager.getInstance().processPayment(this, "test_sub_1");
        } else if (R.id.btn_payment_lv_2 == view.getId()) {
            PaymentManager.getInstance().processPayment(this, "test_sub_2");
        } else {
            PaymentManager.getInstance().processPayment(this, "test_sub_3");
        }
    }


    public void testTokenFetch(String productId, PaymentManager.ResultCallback resultCallback) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        final Gson gson = new Gson();
        Call call = client.newCall(new Request.Builder().url("https://temp.wekey.cn/subscribe").post(RequestBody.create("{\"product_id\":\"" + productId + "\"}", MediaType.parse("application/json"))).addHeader("platform", "Android").build());
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
}
