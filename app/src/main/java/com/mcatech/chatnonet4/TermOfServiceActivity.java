package com.mcatech.chatnonet4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class TermOfServiceActivity extends AppCompatActivity {
    SharedPreferences appSettingSp;
    SharedPreferences.Editor appSettingSpEditor;
    Button acceptButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tos_activity);

        appSettingSp = getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
        appSettingSpEditor = appSettingSp.edit();

        acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appSettingSpEditor.putBoolean("tosLogIn", true);
                appSettingSpEditor.apply();
                Intent intent = new Intent(TermOfServiceActivity.this, AppStarterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}