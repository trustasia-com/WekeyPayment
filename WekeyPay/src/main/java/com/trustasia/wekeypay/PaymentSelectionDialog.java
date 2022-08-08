package com.trustasia.wekeypay;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

public class PaymentSelectionDialog extends BottomSheetDialog implements View.OnClickListener {
    private OnSelectedListener mOnSelectedListener;

    public void setOnSelectedListener(OnSelectedListener mOnSelectedListener) {
        this.mOnSelectedListener = mOnSelectedListener;
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (view.getId() == R.id.tv_alipay) {
            if (mOnSelectedListener != null) mOnSelectedListener.onSelected("alipay");
        }
    }

    public interface OnSelectedListener {
        void onSelected(String payment);
    }

    public PaymentSelectionDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_selection);
        TextView mBtnAlipay = findViewById(R.id.tv_alipay);
        TextView mBtnCancel = findViewById(R.id.tv_cancel);
        Objects.requireNonNull(mBtnCancel).setOnClickListener(this);
        Objects.requireNonNull(mBtnAlipay).setOnClickListener(this);
    }

}
