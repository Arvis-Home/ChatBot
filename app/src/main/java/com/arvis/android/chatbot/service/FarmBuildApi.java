package com.arvis.android.chatbot.service;

import com.arvis.android.chatbot.event.FarmData;
import com.arvis.android.chatbot.event.GetTokenFailed;
import com.arvis.android.chatbot.event.TokenGot;
import com.arvis.android.chatbot.http.HttpsClient;
import com.arvis.android.chatbot.model.FarmDataSample;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.framed.FramedConnection;

/**
 * Created by Jack on 30/7/17.
 */

public class FarmBuildApi implements Callback{

    private static final String clientId = "ARVIS";

    private static final String secret = "sZz[^ZwgP9340lKmB4]+";

    private static final String service = "SOIL_AREA_SERVICES";

    private static final String tokenURL = "https://oauth-fb.agriculture.vic.gov.au/core/connect/token";


    public void getToken(){

        HttpsClient client = new HttpsClient();

        JSONObject content = new JSONObject();

        try {
            content.put("grant_type", "client_credentials");
            content.put("client_id", clientId);
            content.put("client_secret", secret);
            content.put("scope", service);
            client.sendJsonRequestWithCustomHeader(tokenURL, content.toString(), HttpsClient.RequestMethod.POST, this, new HashMap<String, String>());

        } catch (JSONException e) {

            EventBus.getDefault().post(new GetTokenFailed());
        }




    }

    @Override
    public void onFailure(Call call, IOException e) {

        EventBus.getDefault().post(new GetTokenFailed());

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

        if(response.isSuccessful()){

            EventBus.getDefault().post(new TokenGot(response.body().string()));

        }else{
            try {
                EventBus.getDefault().post(new FarmData(new JSONObject(FarmDataSample.farmData)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
