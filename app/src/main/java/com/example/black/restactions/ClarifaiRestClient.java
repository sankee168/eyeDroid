package com.example.black.restactions;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.example.black.constants.Constants;
import com.example.black.projectx.MainActivity;
import com.example.black.projectx.tts;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by sank on 1/23/16.
 */
public class ClarifaiRestClient extends AsyncTask<File,Void,List<String>> {


    Logger logger = Logger.getLogger(ClarifaiRestClient.class.getName());
    private TextToSpeech t1;


    public Map<String, Double> getAllTags(File file) {
        Map<String, Double> allTags = new HashMap<String, Double>();
        ClarifaiClient clarifai = new ClarifaiClient(Constants.RestConstants.APP_ID, Constants.RestConstants.APP_SECRET);
        List<RecognitionResult> results = clarifai.recognize(new RecognitionRequest(file));

        for (Tag tag : results.get(0).getTags()) {
            if (!allTags.keySet().contains(tag.getName())) {
                allTags.put(tag.getName(), tag.getProbability());
            }
        }
        return allTags;
    }

    @Override
    protected List<String> doInBackground(File... file) {
        List<String> finalTopTags = new ArrayList<>();
        Map<String, Double> mapOfAllTags = getAllTags(file[0]);
        Iterator<String> iter = mapOfAllTags.keySet().iterator();
        Map<Double, String> valueList = new HashMap<>();
        while (iter.hasNext()) {
            String currTag = iter.next();
            Double currProb = mapOfAllTags.get(currTag);
            valueList.put(currProb, currTag);
            logger.info(currTag + " : " + currProb);
        }

        List<Double> abc = new ArrayList(valueList.keySet());
        Collections.sort(abc);
        Collections.reverse(abc);

        logger.info("TOP MOST TAGS FOR THE GIVEN PIC");
        Iterator<Double> iter1 = abc.iterator();
        while (iter1.hasNext()) {
            if (finalTopTags.size() * Constants.RestConstants.multiplicationFactor <= mapOfAllTags.keySet().size()) {
                Double value = iter1.next();
                finalTopTags.add(valueList.get(value));
                logger.info(valueList.get(value));
            }
        }
        return finalTopTags;
    }

    @Override
    protected void onPostExecute(List<String> result){
        logger.info(result.toString());


    }
}
