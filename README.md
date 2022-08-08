## Installation

Step 1. Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

```groovy
dependencies {
    implementation 'com.github.trustasia-com:wekey-payment:v1.0.0'
}
```

## Quick Examples

The following code example shows the three main steps to use this SDK :

1. Call `PaymentManager.getInstance().init(BaseUrl,ResultCallback)` to initial SDK.

2. Call `Payment.debuggable()` to enable logs.

3. Call `PaymentManager.getInstance().processPayment(Activity,ProductId)` start payment process.

```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_payment_lv_1).setOnClickListener(this);
        findViewById(R.id.btn_payment_lv_2).setOnClickListener(this);
        findViewById(R.id.btn_payment_lv_3).setOnClickListener(this);

        //Init with baseUrl and tokenFetcher
        PaymentManager.getInstance().init("https://pay-dev.wekey.cn", (productId, callback) -> {
            //Get token for each product id from your server and pass it to callback
        });
        //Enable debug mode
        PaymentManager.debuggable();
    }

    @Override
    public void onClick(View view) {
        //Pass the product id for test 
        if (R.id.btn_payment_lv_1 == view.getId()) {
            PaymentManager.getInstance().processPayment(this, "test_sub_1");
        } else if (R.id.btn_payment_lv_2 == view.getId()) {
            PaymentManager.getInstance().processPayment(this, "test_sub_2");
        } else {
            PaymentManager.getInstance().processPayment(this, "test_sub_3");
        }
    }
}
```
