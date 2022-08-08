package com.trustasia.wekeypay;


import androidx.appcompat.app.AppCompatActivity;

public class PaymentManager {
    private static PaymentManager INSTANCE;

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

    public void processPayment(AppCompatActivity activity, String productId) {
        PaymentSelectionDialog dialog = new PaymentSelectionDialog(activity);
        dialog.setOnSelectedListener(payment -> {
            ContentFragment fragment = ContentFragment.newInstance(productId);
            fragment.show(activity.getSupportFragmentManager(), "Wekey");
        });
        dialog.show();
    }
}
