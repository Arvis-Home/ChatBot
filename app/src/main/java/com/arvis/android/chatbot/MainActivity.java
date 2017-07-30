package com.arvis.android.chatbot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.arvis.android.chatbot.service.TTS;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {

    private AIService aiService;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_AUDIO_PERMISSIONS_ID = 33;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initService();

    }
    private void initService() {

        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN, AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        if (aiService != null) {
            aiService.pause();
        }

        TTS.speak("Good day! I am a chat bot, please talk to me");

        aiService = AIService.getService(this, config);

        aiService.setListener(this);
    }

    @Override
    public void onResult(AIResponse response) {

        final Result result = response.getResult();

        final String speech = result.getFulfillment().getSpeech();

        TTS.speak(speech);

        if(speech.contains("No worries, I will show you on the map soon")){

            startActivity(new Intent(this, MapsActivity.class));

        }else
            handler.postDelayed(runnable, 6000);
    }

    @Override
    public void onError(AIError error) {

        Log.e(TAG, error.getMessage());

        handler.postDelayed(runnable, 3000);
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

        Log.d(TAG, "Listening started");
    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    public void startRecognition() {

        aiService.startListening();
    }

    public void stopRecognition() {
        aiService.stopListening();
    }

    public void cancelRecognition() {
        aiService.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // use this method to disconnect from speech recognition service
        // Not destroying the SpeechRecognition object in onPause method would block other apps from using SpeechRecognition service
        if (aiService != null) {
            aiService.pause();
        }

        handler.removeCallbacks(runnable);

        stopRecognition();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // use this method to reinit connection to recognition service
        if (aiService != null) {
            aiService.resume();
        }

        checkAudioRecordPermission();

    }

    protected void checkAudioRecordPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_AUDIO_PERMISSIONS_ID);

            }
        }else{

            startRecognition();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSIONS_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startRecognition();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startRecognition();
        }
    };
}
