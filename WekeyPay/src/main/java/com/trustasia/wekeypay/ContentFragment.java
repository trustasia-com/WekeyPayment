package com.trustasia.wekeypay;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class ContentFragment extends DialogFragment implements PaymentManager.ResultCallback {
    private String token = null;

    public static ContentFragment newInstance() {
        Bundle args = new Bundle();
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpManager.getInstance().fetchToken((code, result) -> {
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
        if (this.token != null && !this.token.isEmpty()) {
            HttpManager.getInstance().queryPaymentState(token, (code, result) -> {
                if (code == 0) {
                    this.token = null;
                } else {
                    showErrorTips(result);
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    if ("START".equals(result)) {
                        new TipsDialog(requireActivity()).show();
                    } else {
                        new AlertDialog.Builder(requireActivity())
                                .setMessage("订阅未成功！返回重试，或者点击刷新")
                                .setNegativeButton("返回", (dialogInterface, i) -> dialogInterface.dismiss())
                                .setPositiveButton("刷新", (dialogInterface, i) -> dialogInterface.dismiss())
                                .create()
                                .show();
                    }
                    dismiss();
                });
            });
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResult(int code, String result) {
        if (code != 0) {
            showErrorTips(result);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(result));
        startActivity(intent);
    }
}
