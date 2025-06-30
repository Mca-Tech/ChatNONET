package com.mcatech.chatnonet4;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloader {

    private static final String TAG = "FileDownloader";

    public static void download(String fileUrl, Context context) {
        new DownloadFileTask(context).execute(fileUrl);
    }

    private static class DownloadFileTask extends AsyncTask<String, Integer, Void> {

        private Context context;

        public DownloadFileTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            try {
                URL url = new URL(fileUrl);
                URLConnection connection = url.openConnection();
                connection.connect();

                // Get the total file size
                int fileLength = connection.getContentLength();

                // Create a new file in the internal storage
                FileOutputStream outputStream = context.openFileOutput("babyllama.gguf", Context.MODE_PRIVATE);

                // Get the input stream from the connection
                InputStream inputStream = connection.getInputStream();

                // Buffer for reading data from the input stream
                byte[] buffer = new byte[1024];
                int len;
                int downloadedSize = 0;

                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                    downloadedSize += len;
                    // Calculate the download progress
                    int progress = (int) ((downloadedSize / (float) fileLength) * 100);
                    publishProgress(progress);
                }

                // Close streams
                outputStream.close();
                inputStream.close();
                Log.d(TAG, "File downloaded successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error downloading file: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Update the progress indicator (values[0] is the progress percentage)
            int progress = values[0];
            Log.d(TAG, "Download Progress: " + progress + "%");
            // You can update your UI with the progress value here
        }
    }
}