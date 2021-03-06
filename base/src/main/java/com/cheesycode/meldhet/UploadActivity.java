package com.cheesycode.meldhet;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cheesycode.meldhet.helper.ConfigHelper;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.TimerTask;
import java.util.UUID;

import static com.cheesycode.meldhet.IntroActivity.setWindowFlag;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class UploadActivity extends AppCompatActivity {
    FirebaseStorage storage;
    StorageReference storageReference;
    AnimatedCircleLoadingView animatedCircleLoadingView;
    private boolean skipMethod = false;
    private String imagePath;
    private static Location location = null;
    private String issueType;
    private ViewGroup transitionsContainer;
    private TextView title;
    private TextView message;
    private Button button;
    private boolean uploadDone = false;
    private int gpstrycounter = 0;
    private Timer timer;
    private int progress = 0;
    private String filepath;
    private Bitmap imageFileFromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        transitionsContainer = findViewById(R.id.uploadContainer);
        title = transitionsContainer.findViewById(R.id.thankYou);
        message = transitionsContainer.findViewById(R.id.explanation);
        button = transitionsContainer.findViewById(R.id.again);

        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        animatedCircleLoadingView = findViewById(R.id.circle_loading_view);
        storage = FirebaseStorage.getInstance();
        storage.setMaxOperationRetryTimeMillis(3000);
        storage.setMaxUploadRetryTimeMillis(3000);
        storageReference = storage.getReference();
        Intent intent = getIntent();
        filepath = intent.getStringExtra("filepath");
        issueType = intent.getStringExtra("issueType");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                   failedUpload();
                    }
                });
            }
        }, 10*1000);
        if (!skipMethod) {
            if(filepath!= null) {
                uploadImage(filepath);
            }
            else{
                imageFileFromIntent = (Bitmap) intent.getParcelableExtra("file");
                uploadImage(imageFileFromIntent);
            }
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!skipMethod) {
            animatedCircleLoadingView.startDeterminate();
            startLocationUpdates();
        }
        skipMethod = true;
    }

    private void uploadImage(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        uploadByteArray(stream);
    }

    private void uploadImage(String filePath) {

        if (filePath != null) {
            try {
                InputStream is = new FileInputStream(filePath);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                BitmapFactory.decodeStream(is).compress(Bitmap.CompressFormat.JPEG, 50, out);
                uploadByteArray(out);
            } catch (FileNotFoundException e) {
                Log.e("FileIOError",e.toString());
                failedUpload();
            }

        }
    }

    private void uploadByteArray(ByteArrayOutputStream out ){
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        imagePath = ref.getName();
        byte[] bytes = out.toByteArray();
        ref.putBytes(bytes)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadDone = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("FAILURE_ON_UPLOAD", e.getMessage());

                        failedUpload();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (30 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        animatedCircleLoadingView.setPercent((int) Math.round(addProgress(progress)));
                    }
                });
    }

    private int addProgress(double valueToAdd){
        this.progress += valueToAdd;
        if(progress>=100){
            this.progress = 100;
        }
        return progress;
    }

    public void Volleypostfunc() {
                String postUrl = ConfigHelper.getConfigValue(UploadActivity.this, "api_url") + "create/";
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(UploadActivity.this);

                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("id", MessagingService.getToken(UploadActivity.this));
                    jsonBody.put("image", imagePath);
                    jsonBody.put("tag", issueType);
                    jsonBody.put("lat", location.getLatitude());
                    jsonBody.put("lon", location.getLongitude());
                    jsonBody.put("acc", location.getAccuracy());

                    final String mRequestBody = jsonBody.toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, postUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("LOG_VOLLEY", response);
                            animatedCircleLoadingView.stopOk();
                            Transition transition = new Fade(Fade.IN);
                            transition.setStartDelay(1500);
                            transition.setDuration(1500);
                            TransitionManager.beginDelayedTransition(transitionsContainer, transition);
                            title.setVisibility(View.VISIBLE);
                            message.setVisibility(View.VISIBLE);
                            button.setVisibility(View.VISIBLE);
                            final File file = new File(imagePath);
                            if(!file.delete()){Log.e("FILEIO", "File not deleted ");}
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            failedUpload();
                            Log.d("VOLLEY_ERROR", error.toString());

                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                Log.d("ENCODING", uee.toString());
                                failedUpload();
                                throw new AuthFailureError();
                            }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString;
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                            }
                            throw new NullPointerException();
                        }
                    };

                    requestQueue.add(stringRequest);
                } catch (JSONException e) {
                    Log.d("JSON", e.toString());
                    failedUpload();
                }

    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            onLocationChanged(locationResult.getLastLocation());
        }

    };
    private boolean failed = false;

    public void failedUpload(){
        failed = true;
            animatedCircleLoadingView.stopFailure();
            Transition transition = new Fade(Fade.IN);
            transition.setStartDelay(2500);
            transition.setDuration(2000);
            TransitionManager.beginDelayedTransition(transitionsContainer, transition);
            title.setText(getString(R.string.failuretitle));
            title.setVisibility(View.VISIBLE);
            if(gpstrycounter >=5) {
                message.setText(getString(R.string.failuremessage));
            }
            else { message.setText((getString(R.string.noGPS)));}
            message.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            button.setText(getString(R.string.retry));
            button.setBackgroundResource(R.color.colorPrimary);
            getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);

    }

    public void onLocationChanged(Location location) {
        if(gpstrycounter > 5){
            failedUpload();
        }
        gpstrycounter++;
        animatedCircleLoadingView.setPercent(addProgress(gpstrycounter));
        if(UploadActivity.location == null){
            UploadActivity.location = location;}
        if(location.getAccuracy() < UploadActivity.location.getAccuracy()){
            UploadActivity.location = location;
        }
        if(location.getAccuracy() < 15 && gpstrycounter >1){
            if(uploadDone) {
                animatedCircleLoadingView.setPercent(76);
                Volleypostfunc();
                timer.cancel();
                timer.purge();
                getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);
    }

    protected void requestToTurnOnGps(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.gpstitle));
        builder.setMessage(getString(R.string.gpsmessage));

        builder.setPositiveButton(getString(R.string.enable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(onGPS);
                startLocationUpdates();
            }
        });
        builder.setNegativeButton(getString(R.string.disable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                newIssue(null);
            }
        });
        builder.show();
    }

    protected void startLocationUpdates() {

        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            Log.d("GPS", e.toString());
            requestToTurnOnGps();
        }
        if(off==0){
            requestToTurnOnGps();
        }
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(500);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            failedUpload();
            return;
        }
        getFusedLocationProviderClient(this).flushLocations();
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    public void newIssue(View v){
        getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);
        if(!failed) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else{
            Intent intent = new Intent(this, UploadActivity.class);
            intent.putExtra(getString(R.string.issuetyoe), this.issueType);
            if (this.filepath != null ) {
                intent.putExtra(getString(R.string.filepath), this.filepath);
            }
            else {
                intent.putExtra(getString(R.string.file), this.imageFileFromIntent);
            }
                this.startActivity(intent);

        }
    }
}
