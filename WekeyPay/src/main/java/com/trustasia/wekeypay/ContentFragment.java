package com.trustasia.wekeypay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class ContentFragment extends DialogFragment implements PaymentManager.ResultCallback {
    private String token = null;
    private static final String KEY_PRODUCT_ID = "KEY_PRODUCT_ID";
    private boolean called = false;
    private boolean inRequest = false;
    private PaymentManager.PaymentResultCallback mResultCallback;

    public static ContentFragment newInstance(String productId) {
        Bundle args = new Bundle();
        ContentFragment fragment = new ContentFragment();
        args.putString(KEY_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        String productId = getArguments().getString(KEY_PRODUCT_ID, "");
        HttpManager.getInstance().fetchToken(productId, (code, result) -> {
            if (code == 0) {
                this.token = result;
                HttpManager.getInstance().startPayment(result, this);
            } else {
                showErrorTips(result);
            }
        });
    }

    public void showErrorTips(String error) {
        Activity activity = PaymentManager.getInstance().getActivity();
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            String msg = error.replaceFirst("\\([0-9]+\\)", "");
            new AlertDialog.Builder(activity).setMessage("\n" + msg).setNegativeButton("确认", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            }).create().show();

            dismiss();
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new LoadingDialog(requireContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!called) return;
        queryState();
    }

    public void queryState() {
        if (inRequest) return;
        if (this.token != null && !this.token.isEmpty()) {
            inRequest = true;
            String t = token;
            HttpManager.getInstance().queryPaymentState(token, (code, result) -> {
                inRequest = false;
                Activity activity = PaymentManager.getInstance().getActivity();
                if (activity == null) return;
                if (code == 0) {
                    this.token = null;
                } else {
                    showErrorTips(result);
                    return;
                }

                activity.runOnUiThread(() -> {

                    if (Constants.STATE_START.equals(result)) {
                        if (mResultCallback != null) mResultCallback.onResult(Constants.RESULT_SUCCESS);
                        dismiss();
                        new TipsDialog(activity).show();
                    } else {
                        Dialog dialog = new AlertDialog.Builder(activity).setMessage("订阅未成功！返回重试，若已经支付请点击刷新").setNegativeButton("返回", (dialogInterface, i) -> {
                            if (mResultCallback != null) mResultCallback.onResult(Constants.RESULT_FAIL);
                            dismiss();
                            dialogInterface.dismiss();
                        }).setPositiveButton("刷新", (dialogInterface, i) -> {
                            this.token = t;
                            dialogInterface.dismiss();
                            if (mResultCallback != null) mResultCallback.onResult(Constants.RESULT_RETRY);
                            queryState();
                        }).create();
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }
                });
            });
        }
    }

    public void onResult(int code, String result) {
        if (code != 0) {
            showErrorTips(result);
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(result));
            startActivity(intent);
            called = true;
        } catch (Exception e) {
            showErrorTips("支付宝未安装！");
        }
    }

    public void setCallback(PaymentManager.PaymentResultCallback callback) {
        this.mResultCallback = callback;
    }
}
