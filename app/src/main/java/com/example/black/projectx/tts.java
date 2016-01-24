package com.example.black.projectx;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by sandy on 1/23/16.
 */
public class tts {
    TextToSpeech ttobj;
    Context c;

    public tts(Context context) {
        c = context;
        ttobj = new TextToSpeech(c, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    ttobj.setLanguage(Locale.US);
                }
            }
        });
    }


    public void speak(String s) {
        //Toast.makeText(c, toSpeak,Toast.LENGTH_SHORT).show();
        String utteranceId = this.hashCode() + "";
        ttobj.speak(s, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}