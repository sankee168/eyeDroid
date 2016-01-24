package com.example.black.projectx;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.black.constants.Constants;
import com.example.black.restactions.ClarifaiRestClient;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity{
    Logger logger = Logger.getLogger(MainActivity.class.getName());
    EditText ed1;
    Button b1;
    File file;
    List<String> topTags1;
    List<String> topTags2;
    String finalString;
    private final int CHECK_CODE = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed1 = (EditText) findViewById(R.id.editText);
        b1 = (Button) findViewById(R.id.button);

        final Context c = this;
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file = new File("/storage/emulated/0/Pictures/Abc.jpg");
                new Mytask(c).execute(null, null, null);
            }
        });
    }

    public class Mytask extends AsyncTask<Void, Void, Void> {
        Context c;
        TextToSpeech t1;
        Mytask(Context context) {
            c = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            topTags1 = new ClarifaiRestClient().getTopTags(file);
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            topTags2 = topTags1;
            String toSpeak = Constants.RestConstants.DEFAULT_TEXT;
            for (int i = 0; i < topTags2.size(); i++) {
                toSpeak = toSpeak + ", " + topTags2.get(i);
            }
            finalString = toSpeak;
            super.onPostExecute(result);
            t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.US);
                        String utteranceId = this.hashCode() + "";
                        t1.speak(finalString, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
