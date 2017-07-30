package com.arvis.android.chatbot;

import android.app.Application;

import com.arvis.android.chatbot.service.TTS;

/**
 * Created by Jack on 30/7/17.
 */

public class ChatBotApp extends Application{

    @Override
    public void onCreate() {

        super.onCreate();

        TTS.init(this);
    }
}
