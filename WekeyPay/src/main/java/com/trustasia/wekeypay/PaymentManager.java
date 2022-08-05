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

    public void init(TokenFetcher fetcher) {
        HttpManager.getInstance().init(fetcher);
    }

    public static synchronized PaymentManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PaymentManager();
        }
        return INSTANCE;
    }

    public void processPayment(AppCompatActivity activity, String productId) {
        new ActionSheetDialog(activity).builder().addSheetItem("支付宝", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                ContentFragment fragment = ContentFragment.newInstance(productId);
                fragment.show(activity.getSupportFragmentManager(), "Wekey");
            }
        }).show();
    }
}
