package com.jocacast.transcript;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private Spinner spinner;
    private Button goToStoryButton;
    private TextView loadingTextView;
    private int firstResult = 0;
    ArrayList<String> titles = new ArrayList<>();
    ProgressBar progressBar;

    private String germanTranscript;
    private String englishTranscript;
    private String germanTitle;
    private String englishTitle;
    private Uri audioUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        spinner = findViewById(R.id.spinner);
        goToStoryButton = findViewById(R.id.button);
        loadingTextView = findViewById(R.id.menuTitle);
        progressBar = findViewById(R.id.progressBar);
        startLoadingStoriesProcess();

        try{
            ParseQuery <ParseObject> query = ParseQuery.getQuery("Titles");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> resultsList, ParseException e) {
                    for(int i = 0; i < resultsList.size(); i++){
                        ParseObject object = resultsList.get(i);
                        titles.add(object.getString("Title"));
                    }
                    ArrayAdapter adapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_spinner_item,titles);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    endLoadingStoriesProcess();
                }
            });

        }catch(Exception e){
            Log.e("Parse" , "Parse Exception: " + e.getMessage());
        }

        setupListeners();
    }

    public void setupListeners(){
        goToStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadingStoryProcess();
                ParseQuery <ParseObject> transcriptsQuery = ParseQuery.getQuery("Transcripts");
                transcriptsQuery.whereEqualTo("ScrollerTitle", spinner.getSelectedItem().toString());
                transcriptsQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> resultsList, ParseException e) {
                        try{
                            ParseObject object = resultsList.get(firstResult);
                            Log.d("Parse","germanTitle " + object.getString("germanTitle"));
                            getParseTextElements(object);
                            getParseAudioElements(object);
                        }catch(ParseException ex){
                            Log.e("Parse" , "Exception in getParseObjectElements: " + ex.getMessage());
                        }catch(java.io.FileNotFoundException ex){
                            Log.e("Parse", "File Not Found Exception: " +  ex.getMessage());
                        }catch(IOException ex){
                            Log.e("Parse" , "IOException: " + ex.getMessage());
                        }
                        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                        intent.putExtra("germanTranscript", germanTranscript);
                        intent.putExtra("germanTitle", germanTitle);
                        intent.putExtra("englishTranscript", englishTranscript);
                        intent.putExtra("englishTitle", englishTitle);
                        intent.putExtra("audioUri", audioUri);
                        //intent.putExtra("objectId", transcriptId);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void getParseTextElements(ParseObject object) throws ParseException, IOException {
        try{
            ParseFile germanTranscriptFile = object.getParseFile("germanTranscript");
            ParseFile englishTranscriptFile = object.getParseFile("englishTranscript");
            germanTranscript = getTranscriptText(germanTranscriptFile.getFile());
            englishTranscript = getTranscriptText(englishTranscriptFile.getFile());
            germanTitle = object.getString("germanTitle");
            englishTitle = object.getString("englishTitle");

        }catch(ParseException e){
            throw e;
        }catch(java.io.FileNotFoundException e){
            throw e;
        }catch(IOException e) {
            throw e;
        }
    }
    private void getParseAudioElements(ParseObject object){
        ParseFile audioFile = object.getParseFile("audioFile");
        try{
            File file = audioFile.getFile();
            audioUri = Uri.fromFile(file);
            Log.d("Parse", "URI " +audioUri);
        }catch(Exception e){
            Log.d("Parse", e.getMessage());
        }
    }

    private String getTranscriptText(File file) throws java.io.IOException{
        String result = "";
        try{
            BufferedReader br  = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file),"utf-8"));
            String st;
            while ((st = br.readLine())!= null) {
                st += "\n";
                result += st;
            }
        }catch(java.io.FileNotFoundException e){
            throw e;
        }catch (java.io.IOException ex){
            throw ex;
        }
        return result;
    }

    public void startLoadingStoriesProcess(){
        progressBar.setVisibility(View.VISIBLE);
        goToStoryButton.setVisibility(View.VISIBLE);
        goToStoryButton.setEnabled(false);
    }

    public void startLoadingStoryProcess(){
        loadingTextView.setText(R.string.loading_story);
        progressBar.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        goToStoryButton.setVisibility(View.INVISIBLE);
    }


    public void endLoadingStoriesProcess(){
        loadingTextView.setText(R.string.select_text);
        progressBar.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
        goToStoryButton.setEnabled(true);

    }



}
