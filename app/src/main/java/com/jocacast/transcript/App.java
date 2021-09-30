package com.jocacast.transcript;
import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseObject;

import androidx.appcompat.app.AppCompatActivity;


public class App extends Application  {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MainActivity","Parse initialize from App");
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());

        ParseObject firstObject = new  ParseObject("SecondClass");
        firstObject.put("message","Hey ! First message from android. Parse is now connected");
        firstObject.saveInBackground(e -> {
            if (e != null){
                Log.e("MainActivityApp", e.getLocalizedMessage());
            }else{
                Log.d("MainActivityApp","Object saved.");
            }
        });
    }
}
