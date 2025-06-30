package com.mcatech.chatnonet4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    SharedPreferences appSettingSp;
    SharedPreferences.Editor appSettingSpEditor;
    ImageView subscriptionButtonView;
    ImageView changeModelButton;
    ImageView appInfoButtonView;
    RecyclerView mainRecyclerView;
    EditText inputTextView;
    ImageView sendStopButtonView;
    String jniPath;
    LoadingDialogManager loadingDialogManager;
    private RecyclerView recyclerView;
    private int latestMessageIndex = -1;
    private MessageAdapter adapter;
    LinearLayoutManager layoutManager;
    private List<String> messages;
    private String modelName = "";
    public String modelPath = "";
    ImageView swipeUpButton;
    private StringBuilder currentGeneratingText;

    private OutputStreamWriter processInput;
    private BufferedReader processOutput;
    String responseWord = "";
    String responseText = "";
    Boolean displayResponse = false;
    Boolean characterGeneration = false;
    Boolean terminateGeneration = false;
    Boolean modelLoaded = false;
    private Process process;
    String searchLocalDatabaseResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        loadingDialogManager = new LoadingDialogManager(this);

        currentGeneratingText = new StringBuilder();

        appSettingSp = getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
        appSettingSpEditor = appSettingSp.edit();


        initViews();
        getJniPath();
        initRecyclerView();
        buttonListener();
        getModelPath();



    }
    @Override
    protected void onStart() {
        super.onStart();
        checkState();
    }
    public void checkState(){
        checkModelExistence();
    }
    public void checkModelExistence(){
        String selectedModel = appSettingSp.getString("SelectedModel", "");
        if (!selectedModel.isEmpty()) {
            boolean modelDownloaded = appSettingSp.getBoolean(selectedModel, false);
            if (modelDownloaded){

                if (!modelName.equals(selectedModel)){
                    modelName = selectedModel;
                    getModelPath();
                    startModelProcess();
                }


            } else {
                DialogManager.popUp(MainActivity.this, "You did not selected any Model yet!", "Please Select model that suited for your device.", "Select", new DialogManager.DialogCallback() {
                    @Override
                    public void onPositiveButtonPressed() {
                        Intent intent = new Intent(MainActivity.this, ChooseModelActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onNegativeButtonPressed() {

                    }
                });
            }
        }
        else {
            DialogManager.popUp(MainActivity.this, "You did not selected any Model yet!", "Please Select model that suited for your device.", "Select", new DialogManager.DialogCallback() {
                @Override
                public void onPositiveButtonPressed() {
                    Intent intent = new Intent(MainActivity.this, ChooseModelActivity.class);
                    startActivity(intent);
                }
                @Override
                public void onNegativeButtonPressed() {

                }
            });
        }
    }
    public void initViews(){
        subscriptionButtonView = findViewById(R.id.subscriptionButton);
        appInfoButtonView = findViewById(R.id.appInfoButton);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        inputTextView = findViewById(R.id.inputText);
        sendStopButtonView = findViewById(R.id.sendButton);
        changeModelButton = findViewById(R.id.changeModelButton);
        swipeUpButton = findViewById(R.id.swipeDownButton);
    }
    private void getJniPath(){
        PackageManager pm = getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(getPackageName(), PackageManager.GET_SHARED_LIBRARY_FILES);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String nativeLibDir = ai.nativeLibraryDir;
        jniPath = nativeLibDir + "/";
    }
    private void getModelPath() {
        File file = new File(getFilesDir(), modelName);
        modelPath = file.getAbsolutePath();
    }
    private void initRecyclerView(){
        recyclerView = findViewById(R.id.mainRecyclerView);
        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    private void addMessage(String message, String role) {
        currentGeneratingText = new StringBuilder();
        if (role.equals("AI")){
            messages.add(message+" |+|");
        }
        else {
            messages.add(message+ " |userInput|");
        }
        latestMessageIndex = messages.size() - 1;
        adapter.notifyItemInserted(latestMessageIndex);
        recyclerView.smoothScrollToPosition(latestMessageIndex);
    }
    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            adapter.notifyItemChanged(latestMessageIndex);
            updateHandler.postDelayed(this, 100); // Adjust the delay as needed
        }
    };
    private void updateGeneratingMessage(String word, String generatingSign) {
        if (latestMessageIndex >= 0 && latestMessageIndex < messages.size()) {
            currentGeneratingText.setLength(0); // Clear previ
            currentGeneratingText.append(word);
            messages.set(messages.size() - 1, currentGeneratingText.toString() + generatingSign);
            updateHandler.post(updateRunnable);
        } else {
            Log.e("MainActivity", "No messages to replace.");
        }
    }
    public void closeKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void buttonListener(){
        sendStopButtonView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                String userRequest = inputTextView.getText().toString();
                inputTextView.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View arg0, MotionEvent arg1) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(latestMessageIndex);
                                updateHandler.post(updateRunnable);
                            }
                        }, 100);
                        return false;
                    }
                });

                if (!characterGeneration){
                    if (!userRequest.isEmpty()){
                        closeKeyboard(v);
                        terminateGeneration = false;
                        characterGeneration = true;
                        sendStopButtonView.setImageResource(R.drawable.chatnonet_stop_button);
                        sendMessageToModel(userRequest);
                    }
                }
                else if (characterGeneration){
                    terminateGeneration = true;
                }
            }
        });
        subscriptionButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubscriptionActivity.class);
                startActivity(intent);
            }
        });
        appInfoButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AppInformationActivity.class);
                startActivity(intent);
            }
        });
        changeModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseModelActivity.class);
                startActivity(intent);
            }
        });
        swipeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(latestMessageIndex);
            }
        });
        Log.d("CHATNONET4", "Init Buttons /");
    }
    private void sendMessageToModel(String message) {
        new Thread(() -> {
            runOnUiThread(() -> {
                inputTextView.setText("");
                addMessage(message, "");
                addMessage("Responding...", "AI");
            });
            try {
                processInput.write(message + "/"+"\n");
                processInput.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void readModelOutput() {
        new Thread(() -> {
            try {
                while (true) {
                    while (true) {
                        int character = processOutput.read();
                        char c = (char) character;
                        String responseCharacter = String.valueOf(c);

                        if (responseCharacter.isEmpty()){
                            break;
                        }
                        else if (responseCharacter.equals(" ")){
                            responseWord += " ";
                            break;
                        }
                        else if (responseCharacter.equals("\n")){
                            responseWord += "\n";
                            break;
                        }
                        else {
                            responseWord += responseCharacter;
                        }
                    }
                    Log.d("CHATNONET4", responseWord);

                    if (!modelLoaded){
                        responseText += responseWord;
                        Log.d("MODEL LOADING", responseWord);
                        if (responseWord.contains("<|im_end|>") | responseWord.contains("<|eot_id|>") | responseWord.contains("<end_of_turn>")){
                            Log.d("MODEL LOADING", "End token detected /");
                            if (responseText.contains("You are ChatNONET AI assistant.")){
                                Log.d("MODEL LOADING", "Model Loaded /");
                                responseWord = responseWord.replace("<|im_end|>", "").replace("<|eot_id|>", "").replace("<end_of_turn>", "");
                                runOnUiThread(() -> {
                                    updateGeneratingMessage("What can I help?", "|-|");
                                    sendStopButtonView.setImageResource(R.drawable.chatnonet_send_button);
                                    modelLoaded = true;
                                    responseText = "";
                                });
                            }
                        }
                    }


                    else if (modelLoaded) {
                        Log.d("CHATNONET4", responseWord);
                        if (responseWord.contains("/\b/\b")){
                            responseWord = responseWord.replace(responseWord, "");
                            displayResponse = true;

                        }

                        if (displayResponse) {
                            if (responseWord.contains("<|im_end|>") | responseWord.contains("<|eot_id|>") | responseWord.contains("<end_of_turn>")){
                                System.out.println("--------------");
                                responseWord = responseWord.replace("<|im_end|>", "").replace("<|eot_id|>", "").replace("<end_of_turn>", "");
                                responseText += responseWord;
                                displayResponse = false;
                                characterGeneration = false;
                                Log.d("CHATNONET4", "ENDS------------------------");

                                runOnUiThread(() -> {
                                    updateGeneratingMessage(responseText, "|-|");
                                    searchLocalDatabaseResult = "";
                                    responseText = "";
                                    sendStopButtonView.setImageResource(R.drawable.chatnonet_send_button);
                                    responseWord = "";
                                });
                            }
                            else if (!responseText.isEmpty()){
                                responseText += responseWord;
                                runOnUiThread(() -> updateGeneratingMessage(responseText, "|+|"));
                            }
                            else {
                                runOnUiThread(() -> updateGeneratingMessage("Thinking...", "|+|"));
                                responseText += responseWord;
                            }
                        }
                    }

                    responseWord = "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void startModelProcess() {
        new Thread(() -> {
            responseText = "";
            responseWord = "";
            displayResponse = false;
            characterGeneration = false;
            modelLoaded = false;
            runOnUiThread(() -> {
                if (modelName.equals("ChatNONET-135m-tuned-q8_0.gguf")){
                    addMessage("WARNING!\nYou are using the smallest model (135m).The response of this model are mostly inaccurate.", "AI");
                }
                addMessage("Loading Please Wait...", "AI");
                sendStopButtonView.setImageResource(R.drawable.chatnonet_stop_button);

            });
            try {
                process = new ProcessBuilder()
                        .command("." + jniPath + "libllama_cli.so",
                                "-m", modelPath,
                                "-p", "You are ChatNONET AI assistant.",
                                "-cnv",
                                "--special",
                                "--keep", "-1",
                                "--multiline-input"
                        )
                        .redirectErrorStream(true)
                        .start();

                processInput = new OutputStreamWriter(process.getOutputStream());
                processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                readModelOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}

