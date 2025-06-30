package com.mcatech.chatnonet4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AppStarterActivity extends AppCompatActivity {

    SharedPreferences appSettingSp;
    SharedPreferences.Editor appSettingSpEditor;
    LoadingDialogManager loadingDialogManager;
    ImageView startB;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_starter);


        appSettingSp = getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
        appSettingSpEditor = appSettingSp.edit();

        startB = findViewById(R.id.startButton);
        startB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                appSettingSpEditor.putBoolean("appStaterLogIn", true);
                appSettingSpEditor.apply();
                Intent intent = new Intent(AppStarterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

}