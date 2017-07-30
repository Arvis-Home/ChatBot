package com.arvis.android.chatbot;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.arvis.android.chatbot.event.FarmData;
import com.arvis.android.chatbot.event.GetTokenFailed;
import com.arvis.android.chatbot.event.TokenGot;
import com.arvis.android.chatbot.service.FarmApi;
import com.arvis.android.chatbot.service.FarmBuildApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ProgressDialog progressDialog;

    private final String TAG = MapsActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        getFarmData();
    }

    private void getFarmData(){

        progressDialog = ProgressDialog.show(this, null, "Getting farm data...");

        new FarmBuildApi().getToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onTokenGot(TokenGot tokenGot){

        new FarmApi().getFarmData();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFarmData(FarmData data){

        removeProgressDialog();

        JSONArray farmShape = data.farmData.optJSONObject("geometry").optJSONArray("coordinates").optJSONArray(0);

        PolygonOptions polygonOptions = new PolygonOptions();


        for(int i = 0; i < farmShape.length(); i++){

            JSONArray cord = farmShape.optJSONArray(i);

            polygonOptions.add(new LatLng(cord.optDouble(1), cord.optDouble(0)));

        }

        Polygon polygon = mMap.addPolygon(polygonOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polygon.getPoints().get(0), 13));

    }

    private void removeProgressDialog(){

        if(progressDialog != null && progressDialog.isShowing()){

            progressDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void tokenGotFailed(GetTokenFailed failed){

        removeProgressDialog();
    }
}
