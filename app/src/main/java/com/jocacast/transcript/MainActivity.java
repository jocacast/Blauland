package com.jocacast.transcript;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements Runnable{
    private Button startButton;
    private Button stopButton;
    private Button pauseButton;
    private MediaPlayer soundPlayer;
    private SeekBar soundSeekBar;
    private Thread soundThread;
    private TextView transcript;
    private Button englishButton;
    private Button germanButton;
    private TextView title;

    private String germanTranscript;
    private String englishTranscript;
    private String germanTitle;
    private String englishTitle;
    private Uri audioUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);
        pauseButton = findViewById(R.id.pauseButton);
        soundSeekBar = findViewById(R.id.soundSeekBar);
        transcript = findViewById(R.id.taleView);
        englishButton = findViewById(R.id.englishButton);
        germanButton = findViewById(R.id.germanButton);
        pauseButton.setEnabled(false);
        title = findViewById(R.id.title);

        transcript.setMovementMethod(new ScrollingMovementMethod());

        Bundle bundle = getIntent().getExtras();
        String transcriptId = bundle.getString("objectId");
        Uri bundleAudioUri = (Uri) bundle.get("audioUri");
        String bundleGermanTranscript = bundle.getString("germanTranscript");
        String bundleEnglishTranscript = bundle.getString("englishTranscript");
        String bundleGermanTitle = bundle.getString("germanTitle");
        String bundleEnglishTitle = bundle.getString("englishTitle");


        Log.d("Bundle", "bundleUri " + bundleAudioUri);
        Log.d("Bundle", "bundleGermanTranscript " + bundleGermanTranscript);
        Log.d("Bundle", "bundleEnglishTranscript " + bundleEnglishTranscript);
        Log.d("Bundle", "bundleGermanTitle " + bundleGermanTitle);
        Log.d("Bundle", "bundleEnglishTitle " + bundleEnglishTitle);
        Log.d("Bundle" , "bundleObjectId " + transcriptId);

        germanTranscript = bundleGermanTranscript;
        englishTranscript = bundleEnglishTranscript;
        germanTitle = bundleGermanTitle;
        englishTitle = bundleEnglishTitle;
        audioUri = bundleAudioUri;
        transcript.setText(germanTranscript);
        title.setText(germanTitle);

        soundPlayer = new MediaPlayer();
        try{
            soundPlayer.setDataSource(getBaseContext(),audioUri);
            soundPlayer.prepare();
        }catch (Exception ex){
            Log.d("Sound Player Exception " , ex.getMessage());
        }
        runSoundThread();
        setupListeners();

    }

    private void setupListeners(){
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.start();
                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.pause();
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.stop();
                soundPlayer = MediaPlayer.create(getBaseContext(), audioUri);
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
            }
        });

        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transcript.setText(englishTranscript);
                germanButton.setText(R.string.en_germanButton);
                englishButton.setText(R.string.en_englishButton);
                title.setText(englishTitle);

            }
        });

        germanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transcript.setText(germanTranscript);
                germanButton.setText(R.string.de_germanButton);
                englishButton.setText(R.string.de_englishButton);
                title.setText(germanTitle);
            }
        });
        soundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    soundPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


    }


    @Override
    public void run(){
        int currentPosition = 0;
        int soundTotal = soundPlayer.getDuration();
        soundSeekBar.setMax(soundTotal);
        while(soundPlayer != null && currentPosition < soundTotal){
            try{
                Thread.sleep(300 );
                currentPosition = soundPlayer.getCurrentPosition();
            }catch(InterruptedException soundException){
                return;
            }
            catch(Exception e){
                return;
            }
            soundSeekBar.setProgress(currentPosition);
        }

    }

    @Override
    public void onBackPressed() {
        soundPlayer.stop();
        soundThread.interrupt();
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }


    public void runSoundThread(){
        soundThread = new Thread(this);
        soundThread.start();
    }
}
