package com.mcatech.chatnonet4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseModelActivity extends AppCompatActivity {

    private RadioButton nonet1BQ8Model, nonet300mQ8Model, nonet3bQ8Model, nonet135mQ8Model;
    private ImageView backButton;
    public TextView nonet1BQ8DownloadStateView, nonet300mQ8DownloadStateView, nonet3bQ8DownloadStateView, nonet135mQ8DownloadStateView;
    private SharedPreferences appSettingSp;
    private SharedPreferences.Editor appSettingSpEditor;
    TextView unlockPremiumButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_model);

        // Initialize SharedPreferences
        appSettingSp = getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
        appSettingSpEditor = appSettingSp.edit();

        // Initialize UI components
        nonet1BQ8Model = findViewById(R.id.nonet1BQ8Model);
        nonet300mQ8Model = findViewById(R.id.nonet300mQ8Model);
        nonet135mQ8Model = findViewById(R.id.nonet135mQ8Model);
        nonet3bQ8Model = findViewById(R.id.nonet3bQ8Model);
        backButton = findViewById(R.id.backButton);

        nonet1BQ8DownloadStateView = findViewById(R.id.nonet1BQ8DownloadState);
        nonet300mQ8DownloadStateView = findViewById(R.id.nonet300mQ8DownloadState);
        nonet135mQ8DownloadStateView = findViewById(R.id.nonet135mQ8DownloadState);
        nonet3bQ8DownloadStateView = findViewById(R.id.nonet3bQ8DownloadState);


        checkSate();



        // Listener to manage RadioButton selection and save the choice
        View.OnClickListener radioButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Deselect all RadioButtons
                nonet1BQ8Model.setChecked(false);
                nonet300mQ8Model.setChecked(false);
                nonet135mQ8Model.setChecked(false);
                nonet3bQ8Model.setChecked(false);

                // Check the clicked RadioButton and save the selection
                if (view instanceof RadioButton) {
                    RadioButton selectedRadioButton = (RadioButton) view;

                    if (selectedRadioButton == nonet135mQ8Model) {
                        appSettingSpEditor.putString("SelectedModel", "ChatNONET-135m-tuned-q8_0.gguf");
                        appSettingSpEditor.apply();
                        boolean nonet135mQ8ModelDownloaded = appSettingSp.getBoolean("ChatNONET-135m-tuned-q8_0.gguf", false);
                        if (nonet135mQ8ModelDownloaded){
                            nonet135mQ8Model.setChecked(true);
                            nonet135mQ8DownloadStateView.setText("Downloaded");
                            nonet135mQ8DownloadStateView.setTextColor(Color.parseColor("#0080FF"));
                        } else {
                            DialogManager.popUp(ChooseModelActivity.this, "Not downloaded yet!", "Do you want to download it?\nSize -> 135mb", "Download", "Later", new DialogManager.DialogCallback() {
                                @Override
                                public void onPositiveButtonPressed() {
                                    Intent intent = new Intent(ChooseModelActivity.this, DownloadModelActivity.class);
                                    startActivity(intent);

                                }
                                @Override
                                public void onNegativeButtonPressed() {
                                }
                            });
                        }
                    }

                    if (selectedRadioButton == nonet300mQ8Model) {
                        appSettingSpEditor.putString("SelectedModel", "ChatNONET-300m-tuned-q8_0.gguf");
                        appSettingSpEditor.apply();
                        boolean nonet300mQ8ModelDownloaded = appSettingSp.getBoolean("ChatNONET-300m-tuned-q8_0.gguf", false);
                        if (nonet300mQ8ModelDownloaded){
                            nonet300mQ8Model.setChecked(true);
                            nonet300mQ8DownloadStateView.setText("Downloaded");
                            nonet300mQ8DownloadStateView.setTextColor(Color.parseColor("#0080FF"));
                        } else {
                            DialogManager.popUp(ChooseModelActivity.this, "Not downloaded yet!", "Do you want to download it?\nSize -> 300mb", "Download", "Later", new DialogManager.DialogCallback() {
                                @Override
                                public void onPositiveButtonPressed() {
                                    Intent intent = new Intent(ChooseModelActivity.this, DownloadModelActivity.class);
                                    startActivity(intent);

                                }
                                @Override
                                public void onNegativeButtonPressed() {
                                }
                            });
                        }
                    }

                    else if (selectedRadioButton == nonet1BQ8Model) {
                        appSettingSpEditor.putString("SelectedModel", "ChatNONET-1B-tuned-q8_0.gguf");
                        appSettingSpEditor.apply();
                        boolean nonet1BQ8ModelDownloaded = appSettingSp.getBoolean("ChatNONET-1B-tuned-q8_0.gguf", false);
                        if (nonet1BQ8ModelDownloaded){
                            nonet1BQ8Model.setChecked(true);
                            nonet1BQ8DownloadStateView.setText("Downloaded");
                            nonet1BQ8DownloadStateView.setTextColor(Color.parseColor("#0080FF"));
                        } else {
                            DialogManager.popUp(ChooseModelActivity.this, "Not downloaded yet!", "Do you want to download it?\nSize -> 1.2gb", "Download", "Later", new DialogManager.DialogCallback() {
                                @Override
                                public void onPositiveButtonPressed() {
                                    Intent intent = new Intent(ChooseModelActivity.this, DownloadModelActivity.class);
                                    startActivity(intent);
                                }
                                @Override
                                public void onNegativeButtonPressed() {
                                }
                            });
                        }
                    }



                    else if (selectedRadioButton == nonet3bQ8Model) {
                        appSettingSpEditor.putString("SelectedModel", "ChatNONET-3B-tuned-q8_0.gguf");
                        appSettingSpEditor.apply();
                        boolean nonet3BQ8ModelDownloaded = appSettingSp.getBoolean("ChatNONET-3B-tuned-q8_0.gguf", false);
                        if (nonet3BQ8ModelDownloaded){
                            nonet3bQ8Model.setChecked(true);
                            nonet3bQ8DownloadStateView.setText("Downloaded");
                            nonet3bQ8DownloadStateView.setTextColor(Color.parseColor("#0080FF"));
                        } else {
                            DialogManager.popUp(ChooseModelActivity.this, "Not Downloaded Yet!", "Do you want to download it?\nSize -> 3.18gb", "Download", "Later", new DialogManager.DialogCallback() {
                                @Override
                                public void onPositiveButtonPressed() {
                                    Intent intent = new Intent(ChooseModelActivity.this, DownloadModelActivity.class);
                                    startActivity(intent);
                                }
                                @Override
                                public void onNegativeButtonPressed() {
                                }
                            });
                        }
                    }

                }
            }
        };

        // Assign listeners to RadioButtons
        nonet1BQ8Model.setOnClickListener(radioButtonListener);
        nonet300mQ8Model.setOnClickListener(radioButtonListener);
        nonet135mQ8Model.setOnClickListener(radioButtonListener);
        nonet3bQ8Model.setOnClickListener(radioButtonListener);

        // Back button functionality
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSate();
    }

    public void checkSate(){
        // Restore the previously selected model
        String selectedModel = appSettingSp.getString("SelectedModel", "");
        if (appSettingSp.getBoolean("ChatNONET-1B-tuned-q8_0.gguf", false)){
            nonet1BQ8DownloadStateView.setText("Downloaded");
            nonet1BQ8DownloadStateView.setTextColor(Color.parseColor("#0080FF"));
            if (selectedModel.equals("ChatNONET-1B-tuned-q8_0.gguf")) {
                nonet1BQ8Model.setChecked(true);
            }
        }
        if (appSettingSp.getBoolean("ChatNONET-135m-tuned-q8_0.gguf", false)){
            nonet135mQ8DownloadStateView.setText("Downloaded");
            nonet135mQ8DownloadStateView.setTextColor(Color.parseColor("#0080FF"));
            if (selectedModel.equals("ChatNONET-135m-tuned-q8_0.gguf")) {
                nonet135mQ8Model.setChecked(true);
            }
        }
        if (appSettingSp.getBoolean("ChatNONET-300m-tuned-q8_0.gguf", false)){
            nonet300mQ8DownloadStateView.setText("Downloaded");
            nonet300mQ8DownloadStateView.setTextColor(Color.parseColor("#0080FF"));
            if (selectedModel.equals("ChatNONET-300m-tuned-q8_0.gguf")) {
                nonet300mQ8Model.setChecked(true);
            }
        }
        if (appSettingSp.getBoolean("ChatNONET-3B-tuned-q8_0.gguf", false)){
            nonet3bQ8DownloadStateView.setText("Downloaded");
            nonet3bQ8DownloadStateView.setTextColor(Color.parseColor("#0080FF"));
            if (selectedModel.equals("ChatNONET-3B-tuned-q8_0.gguf")) {
                nonet3bQ8Model.setChecked(true);
            }
        }
    }
}
