package com.cheesycode.meldhet;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cheesycode.meldhet.helper.ConfigHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cheesycode.meldhet.IntroActivity.setWindowFlag;

public class ChatActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private RecyclerView chatcontainer;
    private ConstraintLayout chatView;
    private Map<MarkerOptions, String> markers;
    private String[] tests = new String[]{"Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3","Test1", "Test2", "Test3"};
    private EditText newMessage;
    private Button send;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CardView cardView;
    private String issueidfrompush;
    private static MarkerOptions pushMarker = null;
    private ArrayList<Marker> marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRequest(ConfigHelper.getConfigValue(this, "api_url") + "get?id=" + MessagingService.getToken(this), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processIssueList(response);
            }
        });
        setContentView(R.layout.activity_chat);
        marker = new ArrayList<Marker>();
        issueidfrompush = this.getIntent().getStringExtra("ISSUEID");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.cancelAll();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        send = findViewById(R.id.sendnewmessage);
        cardView = findViewById(R.id.card_view);
        newMessage = findViewById(R.id.newmessagebox);
        chatView =  findViewById(R.id.chatView);
        chatcontainer = findViewById(R.id.chatContainer);
        mLayoutManager = new LinearLayoutManager(this);
        chatcontainer.setLayoutManager(mLayoutManager);

        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        Toast.makeText(this, "Een ogenblikje geduld alstublieft", Toast.LENGTH_SHORT);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if(markers != null){
            processIssueList();
        }
    }

    public boolean clickMarkerOptions(LatLng marker){
        String issue = "";
        for(MarkerOptions m : markers.keySet()){
            if(m.getPosition().equals(marker)){
                issue = markers.get(m);
            }
        }
        getRequest(ConfigHelper.getConfigValue(this, "message_url") + "getall?issue=" + issue,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mAdapter = new ChatAdapter(tests);
                        chatcontainer.setAdapter(mAdapter);
                        chatcontainer.smoothScrollToPosition(mAdapter.getItemCount()-1);
                        mAdapter.notifyDataSetChanged();
                    }
                });
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(chatView);
        constraintSet.connect(R.id.card_view,ConstraintSet.BOTTOM,R.id.guideline2,ConstraintSet.TOP,0);
        constraintSet.connect(R.id.chatContainer,ConstraintSet.TOP,R.id.guideline2,ConstraintSet.BOTTOM,0);
        TransitionManager.beginDelayedTransition(chatView);
        constraintSet.applyTo(chatView);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker,15));
        send.setVisibility(View.VISIBLE);
        newMessage.setVisibility(View.VISIBLE);

        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for(Marker m : this.marker){
            m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        return clickMarkerOptions(marker.getPosition());
    }

    public void getRequest(String getUrl, Response.Listener<String> listener){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getUrl, listener
               , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
// Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    public void processIssueList(String response){
        try {
            //TODO DESERIALIZE RESPONSE
            Gson gson = new Gson();
            List<Issue> issueList = gson.fromJson(response, new TypeToken<List<Issue>>() {
            }.getType());
            markers = new HashMap<MarkerOptions, String>();
            for (Issue i : issueList) {
                if (i.id.equals(issueidfrompush)) {
                    ChatActivity.pushMarker = new MarkerOptions().title("#" + i.tag).position(new LatLng(i.lat, i.lon));
                    markers.put(new MarkerOptions().title("#" + i.tag).position(new LatLng(i.lat, i.lon)), i.id);
                }
                else {
                    markers.put(new MarkerOptions().title("#" + i.tag).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(new LatLng(i.lat, i.lon)), i.id);
                }
            }
            if (mMap != null) {
                processIssueList();
            }
        }
        catch(Exception e){
            Log.d("FUCKUP","SOMETHING WONG WITH MAP. CHINA PEOPLE SHOULD WORK HARDER");
        }
    }

    private void processIssueList(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (MarkerOptions mo : markers.keySet()) {
            builder.include(mo.getPosition());
            marker.add(mMap.addMarker(mo));
        }
        LatLngBounds bounds = builder.build();
        int padding = 200;

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        if (ChatActivity.pushMarker != null) {
            clickMarkerOptions(ChatActivity.pushMarker.getPosition());
        }
        else {
            mMap.animateCamera(cu);
        }
    }
}