package com.trustasia.wekeypay;

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
        requireActivity().runOnUiThread(() -> {
            String msg = error.replaceFirst("\\([0-9]+\\)", "");
            new AlertDialog.Builder(requireActivity()).setMessage("\n" + msg).setNegativeButton("确认", (dialogInterface, i) -> {
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
        queryState();
    }

    public void queryState() {
        if (this.token != null && !this.token.isEmpty()) {
            String t = token;
            HttpManager.getInstance().queryPaymentState(token, (code, result) -> {
                if (code == 0) {
                    this.token = null;
                } else {
                    showErrorTips(result);
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    if (Constants.STATE_START.equals(result)) {
                        new TipsDialog(requireActivity()).show();
                        dismiss();
                    } else {
                        new AlertDialog.Builder(requireActivity()).setMessage("订阅未成功！返回重试，若已经支付请点击刷新").setNegativeButton("返回", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            dismiss();
                        }).setPositiveButton("刷新", (dialogInterface, i) -> {
                            this.token = t;
                            dialogInterface.dismiss();
                            queryState();
                        }).create().show();
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
        } catch (Exception e) {
            showErrorTips("支付宝未安装！");
        }
    }
}
