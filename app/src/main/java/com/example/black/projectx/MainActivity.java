package com.example.black.projectx;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.black.constants.Constants;
import com.example.black.restactions.ClarifaiRestClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static Logger logger = Logger.getLogger(MainActivity.class.getName());
    File file;
    String mCurrentPhotoPath;
    List<String> topTags1;
    List<String> topTags2;
    String finalString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            galleryAddPic();
            file = new File(mCurrentPhotoPath);
            new Mytask(getApplicationContext()).execute(null, null, null);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //TODO: Use Custom Intent rather than default camera intent.
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
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
            t1 = new TextToSpeech(c, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}

