package com.trustasia.wekeypayment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.trustasia.wekeypay.HttpManager;
import com.trustasia.wekeypay.PaymentManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_payment_lv_1).setOnClickListener(this);
        findViewById(R.id.btn_payment_lv_2).setOnClickListener(this);
        findViewById(R.id.btn_payment_lv_3).setOnClickListener(this);
        PaymentManager.getInstance().init(callback -> {
            HttpManager.getInstance().testToken(callback);
        });
    }

    @Override
    public void onClick(View view) {
        PaymentManager.getInstance().processPayment(this);
    }

}
