package com.mcatech.chatnonet4;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechHelper {

    private TextToSpeech textToSpeech;

    public TextToSpeechHelper(Context context) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US); // Set the desired language
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported or missing data.");
                    }
                } else {
                    Log.e("TTS", "Initialization failed.");
                }
            }
        });
    }

    public void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void stop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    public void release() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }
}
