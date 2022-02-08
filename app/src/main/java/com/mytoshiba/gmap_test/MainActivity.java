package com.mytoshiba.gmap_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    private GoogleApiClient googleApiClient;
    Data data;

    String str_locality;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gmap);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {

            getCurrentLocation();


        }else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},44);
        }


    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude()
                            ,location.getLongitude());
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        1);
                            } catch (Exception ioException) {
                                Log.e("", "Error in getting address for the location");
                            }

                            if (addresses == null || addresses.size() == 0) {
                                String errormessgae = "No address found for the location";
                                Toast.makeText(MainActivity.this, "" + errormessgae, Toast.LENGTH_SHORT).show();
                            } else {
                                Address address = addresses.get(0);
                                str_locality = address.getLocality();
                               data = new Data(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),str_locality,"Saran");

                               Toast.makeText(getApplicationContext(),String.valueOf(location.getLatitude())+" "  + String.valueOf(location.getLongitude())+" "+str_locality+" "+"Saran",Toast.LENGTH_LONG).show();
                            }

                            MarkerOptions options = new MarkerOptions().position(latLng).title("you are here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                            googleMap.addMarker(options);
                            postData();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                getCurrentLocation();
//                requestGPSSettings();
            }
        }
    }
//    private GoogleApiClient getAPIClientInstance() {
//        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API).build();
//        return mGoogleApiClient;
//    }
    private void requestGPSSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(500);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("", "All location settings are satisfied.");
                        Toast.makeText(getApplication(), "GPS is already enable", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("Applicationsett", e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//
                        break;
                }
            }
        });
    }
    private void postData(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitApi.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitAPI = retrofit.create(RetrofitApi.class);
        Call<ResponseBody> call = retrofitAPI.createPost(data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                 String ss =response.toString();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}