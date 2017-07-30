package com.arvis.android.chatbot.service;

import com.arvis.android.chatbot.event.FarmData;
import com.arvis.android.chatbot.http.HttpsClient;
import com.arvis.android.chatbot.model.FarmDataSample;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Jack on 30/7/17.
 */

public class FarmApi implements Callback{

    private static final String url = "https://farmbuild-soil.agriculture.vic.gov.au/areas";

    public void getFarmData(String token){

        HttpsClient client = new HttpsClient();

        try {
            client.sendJsonRequestWithCustomHeader(url, new JSONObject(FarmDataSample.farmData).toString(), HttpsClient.RequestMethod.POST, this, new HashMap<String, String>());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

        if(response.isSuccessful()){

            try {

                EventBus.getDefault().post(new FarmData(new JSONObject(response.body().string())));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
