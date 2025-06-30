package com.mcatech.chatnonet4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    SharedPreferences appSettingSp;
    SharedPreferences.Editor appSettingSpEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        appSettingSp = getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
        appSettingSpEditor = appSettingSp.edit();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!appSettingSp.getBoolean("tosLogIn", false)){
                    Intent intent = new Intent(SplashScreenActivity.this, TermOfServiceActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (!appSettingSp.getBoolean("appStaterLogIn", false)){
                    Intent intent = new Intent(SplashScreenActivity.this, AppStarterActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (!appSettingSp.getBoolean("downloadModel", false)){
                    Intent intent = new Intent(SplashScreenActivity.this, ChooseModelActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);

    }
}