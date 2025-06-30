package com.mcatech.chatnonet4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class DownloadModelActivity extends AppCompatActivity {

    SharedPreferences appSettingSp;
    SharedPreferences.Editor appSettingSpEditor;
    private static final String TAG = "FileDownloader";
    String jniPath;

    TextView downloadProgressDisplay;
    TextView downloadTextInfoDisplay;
    Button pauseResumeButton;

    public String modelName = "";
    public String downloadSrc = "https://huggingface.co/McaTech/Nonet/resolve/main/";
    public String modelPath = "";

    private DownloadFileTask downloadTask;
    LoadingDialogManager loadingDialogManager;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_download_model);

        appSettingSp = getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
        appSettingSpEditor = appSettingSp.edit();

        modelName = appSettingSp.getString("SelectedModel", "");

        loadingDialogManager = new LoadingDialogManager(this);

        downloadProgressDisplay = findViewById(R.id.downloadProgress);
        downloadTextInfoDisplay = findViewById(R.id.downloadTextInfo);

        pauseResumeButton = findViewById(R.id.pauseResumeButton);

        getJniPath();
        getModelPath();

        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonStatus = pauseResumeButton.getText().toString();
                if (buttonStatus.equals("PAUSE")) {
                    if (downloadTask != null) {
                        downloadTextInfoDisplay.setText("Click 'RESUME' to continue downloading.");
                        pauseResumeButton.setText("RESUME");
                        downloadTask.cancel(true);
                    }
                } else if (buttonStatus.equals("RESUME")) {
                    if (downloadTask != null && downloadTask.isCancelled()) {
                        pauseResumeButton.setText("PAUSE");
                        downloadProgressDisplay.setText("wait");
                        downloadTask = new DownloadFileTask(DownloadModelActivity.this);
                        downloadTask.execute(downloadSrc+modelName);
                    }
                }
            }
        });

        downloadTextInfoDisplay.setText("Connecting to McaTech Server...");
        downloadProgressDisplay.setText("--");
        beginDownload();
    }



    public void beginDownload() {
        new Handler(Looper.getMainLooper()).post(() -> {
            pauseResumeButton.setVisibility(View.VISIBLE);
            download(downloadSrc+modelName, DownloadModelActivity.this);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getJniPath() {
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

    private boolean modelExists(String fileName) {
        File file = new File(getFilesDir(), fileName);
        return file.exists();
    }

    private void download(String fileUrl, Context context) {
        downloadTask = new DownloadFileTask(context);
        downloadTask.execute(fileUrl);
    }

    private class DownloadFileTask extends AsyncTask<String, Integer, Void> {
        private Context context;
        public DownloadFileTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                File file = new File(context.getFilesDir(), modelName);
                long downloadedSize = 0;

                if (file.exists()) {
                    downloadedSize = file.length();
                    connection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");
                }

                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_PARTIAL || responseCode == HttpURLConnection.HTTP_OK) {
                    RandomAccessFile outputStream = new RandomAccessFile(file, "rw");
                    outputStream.seek(downloadedSize);

                    InputStream inputStream = connection.getInputStream();
                    byte[] buffer = new byte[8192];  // Increase buffer size for efficiency
                    int len;
                    long totalSize = downloadedSize + connection.getContentLengthLong(); // Use getContentLengthLong() for large files

                    while ((len = inputStream.read(buffer)) != -1) {
                        if (isCancelled()) {
                            inputStream.close();
                            outputStream.close();
                            return null;
                        }
                        outputStream.write(buffer, 0, len);
                        downloadedSize += len;

                        // Safely calculate progress percentage
                        double progress = ((double) downloadedSize / totalSize) * 100;
                        publishProgress((int) (progress * 10000)); // Publish progress with high precision
                    }

                    outputStream.close();
                    inputStream.close();
                    Log.d(TAG, "File downloaded successfully");

                    appSettingSpEditor.putBoolean(modelName, true);
                    appSettingSpEditor.putBoolean("downloadModel", true);
                    appSettingSpEditor.apply();

                    new Handler(Looper.getMainLooper()).post(() -> {
                        pauseResumeButton.setVisibility(View.GONE);
                        downloadTextInfoDisplay.setText("Initiating...Please Wait...");
                        downloadProgressDisplay.setText("SUCCESS!");
                    });

                    finish();

                } else {
                    downloadTextInfoDisplay.setText("ERROR!!\n\nServer returned HTTP " + responseCode + "\n\nTry restarting the app!!");
                    throw new Exception("Server returned HTTP " + responseCode);
                }
            } catch (Exception e) {
                downloadTask.cancel(true);
                new Handler(Looper.getMainLooper()).post(() -> {
                    pauseResumeButton.setText("RESUME");
                    downloadTextInfoDisplay.setText("Error connecting to McaTech Server!\n\nCheck your internet connection and click 'RESUME'!");
                    downloadProgressDisplay.setText("ERROR!!");
                });
                Log.e(TAG, "Error downloading file: " + e.getMessage() + "\n\nMake sure the Internet Connection is stable!\n\nYou must restart the APP and download it again!");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            double actualProgress = progress / 10000.0; // Correct the decimal value by dividing by 10000
            DecimalFormat decimalFormat = new DecimalFormat("0.000");
            String formattedProgress = decimalFormat.format(actualProgress);
            downloadProgressDisplay.setText(formattedProgress + "%");
            downloadTextInfoDisplay.setText("DOWNLOADING...");
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
    }
}
