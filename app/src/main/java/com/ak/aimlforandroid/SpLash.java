package com.ak.aimlforandroid;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ak.aimlforandroid.UI.HOME.Home;
import com.ak.aimlforandroid.UI.LOGIN.Login;
import com.ak.aimlforandroid.Untils.Constants;


public class SpLash extends AppCompatActivity {

    private static final long DELAY_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_lash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Constants.AUTH.getCurrentUser()==null){
                    startActivity(new Intent(SpLash.this, Login.class));
                    finishAffinity();
                }else {
                    startActivity(new Intent(SpLash.this, Home.class));
                    finishAffinity();
                }
            }
        },DELAY_TIME);
    }
}