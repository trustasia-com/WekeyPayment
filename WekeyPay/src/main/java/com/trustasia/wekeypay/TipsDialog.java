package com.trustasia.wekeypay;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public class TipsDialog extends Dialog {
    public TipsDialog(@NonNull Context context) {
        super(context, R.style.BaseDialog);
        setContentView(R.layout.dialog_tips);
        setCanceledOnTouchOutside(false);
    }

    public void show() {
        super.show();
        new Handler(Looper.getMainLooper()).postDelayed(this::dismiss, 1500);
    }

}
