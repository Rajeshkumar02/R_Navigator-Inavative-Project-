package com.example.r_navigater;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{

    Button btnLogOut;
    FirebaseAuth mAuth;
    TextView txt;

    Dialog myDialog;

    //Location
    FusedLocationProviderClient mFusedLocationClient;
    TextView lati;
    TextView longi;
    int PERMISSION_ID = 44;
    public  String lat;
    public String log;
    public String ldate;
    public String ltime;
    public String lname;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogOut = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();

        myDialog = new Dialog(this);

        //Location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        //SignOut
        btnLogOut.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
        });
    }

    //Executed when the application starts
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        //If not the Current User
        if (user == null){
            startActivity(new Intent(MainActivity.this, Login.class));
        }

        String name[] = user.getEmail().split("@");
        txt=findViewById(R.id.wecome);
        txt.setText("Welcome back "+name[0]+" !");

        //Date&Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String Date_Time = dtf.format(now);
        String D_T[] = Date_Time.split(" ");
        ldate=D_T[0];
        ltime=D_T[1];
        lname = name[0];
    }

    //Popup
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ShowPopup(View v) {
        TextView txtclose,date,time;
        myDialog.setContentView(R.layout.custompopup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");

        //Location
        lati = (TextView) myDialog.findViewById(R.id.latitude);
        longi = (TextView) myDialog.findViewById(R.id.longitude);
        lati.setText(lat);
        longi.setText(log);

        //Date&Time
        date = (TextView) myDialog.findViewById(R.id.date);
        time = (TextView) myDialog.findViewById(R.id.time);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String Date_Time = dtf.format(now);
        String D_T[] = Date_Time.split(" ");
        date.setText(D_T[0]);
        time.setText(D_T[1]);

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    //Location
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lat=location.getLatitude() + "";
                            log=location.getLongitude() + "";
//                            latitudeTextView.setText(location.getLatitude() + "");
//                            longitTextView.setText(location.getLongitude() + "");

                            //Push to firebase
                            Map<String, String> data = new HashMap<>();
                            data.put(ltime, lat+","+log);
                            FirebaseFirestore.getInstance().collection(lname).document(ldate)
                                    .set(data, SetOptions.merge());
                            Map<String, String> data1 = new HashMap<>();
                            data1.put("Date",ldate);
                            data1.put("Time",ltime);
                            data1.put("Latitude",lat);
                            data1.put("Longitude",log);
                            FirebaseFirestore.getInstance().collection(lname).document("Last")
                                    .set(data1);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat=mLastLocation.getLatitude() + "";
            log=mLastLocation.getLongitude() + "";

            //Push to firebase
            Map<String, String> data = new HashMap<>();
            data.put(ltime, lat+","+log);
            FirebaseFirestore.getInstance().collection(lname).document(ldate)
                    .set(data, SetOptions.merge());

            Map<String, String> data1 = new HashMap<>();
            data1.put("Date",ldate);
            data1.put("Time",ltime);
            data1.put("Latitude",lat);
            data1.put("Longitude",log);
            FirebaseFirestore.getInstance().collection(lname).document("Last")
                    .set(data1);

//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
//            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}