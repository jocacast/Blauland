package com.jocacast.transcript;

import android.content.Intent;
import android.net.Uri;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
        setContentView(R.layout.activity_loading);
        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("Title");

        /*ParseObject object = (ParseObject)bundle.get("title");
        object.getString("germanTitle");
        Log.d("LoadingActivity", object.getString("germanTitle"));*/


        ParseQuery <ParseObject> query = ParseQuery.getQuery("Titles");
        query.whereEqualTo("Title", title);
        try{
            List<ParseObject> resultsList =  query.find();
            ParseObject object = resultsList.get(0);
            ParseRelation transcriptRelation = object.getRelation("transcriptId");
            List<ParseObject> transcript = transcriptRelation.getQuery().find();
            ParseObject transcriptObject = transcript.get(0);
            Log.d("Parse", "English Title: " + transcriptObject.getString("englishTitle"));
            Log.d("Parse", "German Title: " + transcriptObject.getString("germanTitle"));
            Log.d("Parse",  "Object Id: " + transcriptObject.getObjectId());

            /*
            ParseFile audioFile = object.getParseFile("audioFile");
            try{
                File file = audioFile.getFile();
            }catch(Exception e){
                Log.d("Parse", e.getMessage());
            }*/

            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
            intent.putExtra("objectId", transcriptObject.getObjectId());
            startActivity(intent);

        }catch(Exception e){
            Log.e("Parse", "Parse Exception " + e.getMessage());
        }

    }
}
