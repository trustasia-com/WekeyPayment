package com.trustasia.wekeypay;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.SoftReference;

public class PaymentManager {
    private static PaymentManager INSTANCE;
    private SoftReference<AppCompatActivity> acts;

    public interface PaymentResultCallback {
        void onResult(String state);
    }


    public interface ResultCallback {
        void onResult(int code, String result);
    }

    public interface TokenFetcher {
        void getToken(String productId, ResultCallback callback);
    }

    private PaymentManager() {
    }

    public void init(String baseUrl, TokenFetcher fetcher) {
        HttpManager.getInstance().init(baseUrl, fetcher);
    }

    public static synchronized PaymentManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PaymentManager();
        }
        return INSTANCE;
    }

    public static void debuggable() {
        HttpManager.getInstance().debuggable();
    }

    public AppCompatActivity getActivity() {
        if (acts == null) return null;
        return acts.get();
    }

    public void processPayment(AppCompatActivity activity, String productId) {
        this.processPayment(activity, productId, null);
    }

    public void processPayment(AppCompatActivity activity, String productId, @Nullable PaymentResultCallback callback) {
        acts = new SoftReference<>(activity);
        PaymentSelectionDialog dialog = new PaymentSelectionDialog(activity);
        dialog.setOnSelectedListener(payment -> {
            ContentFragment fragment = ContentFragment.newInstance(productId);
            fragment.setCallback(callback);
            fragment.show(activity.getSupportFragmentManager(), "Wekey");

        });
        dialog.show();
    }
}
