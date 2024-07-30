package com.example.demo_app;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.demo_app.databinding.ActivityMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class Map_Activity extends FragmentActivity implements OnMapReadyCallback {

    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    MarkerOptions myLocationMarker;
    Marker ganaGeneralHospitalMarker,ShreeKrishnaHospitalMarker,BhanubhaiandMadhubenPatelCardiacCentreMarker,H_M_PatelCenterforMedicalCareandEducationMarker,DrSanjayBhoiMarker,surajbenHospitalMarker,SarvajanikHospitalAgasMarker,NewMaheshwariFemaleHospitalMarker,GidcClinicMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        com.example.demo_app.databinding.ActivityMapBinding binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);

            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;

                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    assert mapFragment != null;
                    mapFragment.getMapAsync(Map_Activity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Map_Activity.this,R.raw.map_style));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setPadding(0,30,0,10);

        if (currentLocation != null) {
            yourLocation(googleMap);
        }
        // call all method so all marker appear in map
        addSurajbenHospitalMarker(googleMap);
        addGanaGeneralHospitalMarker(googleMap);
        addSarvajanikHospitalAgasMarker(googleMap);
        addNewMaheshwariFemaleHospitalMarker(googleMap);
        addGidcClinicMarker(googleMap);
        addDrSanjayBhoiMarker(googleMap);
        addH_M_PatelCenterforMedicalCareandEducationMarker(googleMap);
        addBhanubhaiandMadhubenPatelCardiacCentreMarker(googleMap);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null; // Use default info window
            }

            @Override
            public View getInfoContents(@NonNull Marker marker) {
                // Inflate custom info window layout
                View infoWindowView = getLayoutInflater().inflate(R.layout.activity_map, null);

                // Set content for the info window
                TextView textViewTitle = infoWindowView.findViewById(R.id.pop_up_at_location);
                textViewTitle.setText(marker.getTitle());

                // Set the size of the info window programmatically
                int width = getResources().getDimensionPixelSize(R.dimen.info_window_width);
                int height = getResources().getDimensionPixelSize(R.dimen.info_window_height);
                infoWindowView.setLayoutParams(new LinearLayout.LayoutParams(width, height));

                return infoWindowView;
            }
        });

        // Show info window when the hospital marker is clicked
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                marker.showInfoWindow();
                return true; // Indicates that the click event has been consumed
            }
        });

        // Handle clicks on the info window
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                if (marker.equals(surajbenHospitalMarker)) {
                    redirectToDetailPage(surajbenHospitalMarker.getTitle());
                } else if (marker.equals(ganaGeneralHospitalMarker)) {
                    redirectToDetailPage(ganaGeneralHospitalMarker.getTitle());
                }else if (marker.equals(SarvajanikHospitalAgasMarker)) {
                    redirectToDetailPage(SarvajanikHospitalAgasMarker.getTitle());
                }else if (marker.equals(NewMaheshwariFemaleHospitalMarker)) {
                    redirectToDetailPage(NewMaheshwariFemaleHospitalMarker.getTitle());
                }else if (marker.equals(GidcClinicMarker)) {
                    redirectToDetailPage(GidcClinicMarker.getTitle());
                }else if (marker.equals(DrSanjayBhoiMarker)) {
                    redirectToDetailPage(DrSanjayBhoiMarker.getTitle());
                }else if (marker.equals(H_M_PatelCenterforMedicalCareandEducationMarker)) {
                    redirectToDetailPage(H_M_PatelCenterforMedicalCareandEducationMarker.getTitle());
                }else if (marker.equals(BhanubhaiandMadhubenPatelCardiacCentreMarker)) {
                    redirectToDetailPage(BhanubhaiandMadhubenPatelCardiacCentreMarker.getTitle());
                }else if (marker.equals(ShreeKrishnaHospitalMarker)) {
                    redirectToDetailPage(ShreeKrishnaHospitalMarker.getTitle());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Toast.makeText(this,"location permission is denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void yourLocation(@NonNull GoogleMap googleMap){
        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myLocationMarker = new MarkerOptions().position(myLocation).title("Your Location");
        Marker location = googleMap.addMarker(myLocationMarker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16f));
    }
    private void addSurajbenHospitalMarker(@NonNull GoogleMap googleMap) {
        LatLng surajbenHospitalLocation = new LatLng(22.51938, 72.91790);
        MarkerOptions surajbenHospitalMarkerOptions = new MarkerOptions()
                .position(surajbenHospitalLocation)
                .title("Surajben Govindbhai Patel Ayurvedic Hospital");
        surajbenHospitalMarker = googleMap.addMarker(surajbenHospitalMarkerOptions);
    }
    private void addGanaGeneralHospitalMarker(@NonNull GoogleMap googleMap) {
        LatLng ganaGeneralHospitalLocation = new LatLng(22.509145582918197, 72.9178335517697);
        MarkerOptions ganaGeneralHospitalMarkerOptions = new MarkerOptions().position(ganaGeneralHospitalLocation)
                .title("Gana General Hospital");
        ganaGeneralHospitalMarker = googleMap.addMarker(ganaGeneralHospitalMarkerOptions);
    }
    private void addSarvajanikHospitalAgasMarker(@NonNull GoogleMap googleMap) {
        LatLng SarvajanikHospitalAgasLocation = new LatLng(22.503190798003402, 72.88402678016517);
        MarkerOptions SarvajanikHospitalAgasMarkerOptions = new MarkerOptions().position(SarvajanikHospitalAgasLocation)
                .title("SARVAJANIK HOSPITAL , AGAS");
        SarvajanikHospitalAgasMarker = googleMap.addMarker(SarvajanikHospitalAgasMarkerOptions);
    }
    private void addNewMaheshwariFemaleHospitalMarker(@NonNull GoogleMap googleMap) {
        LatLng NewMaheshwariFemaleHospitalLocation = new LatLng(22.52687115412498, 72.92213205002034);
        MarkerOptions NewMaheshwariFemaleHospitalMarkerOptions = new MarkerOptions().position(NewMaheshwariFemaleHospitalLocation)
                .title("SARVAJANIK HOSPITAL , AGAS");
        NewMaheshwariFemaleHospitalMarker = googleMap.addMarker(NewMaheshwariFemaleHospitalMarkerOptions);
    }
    private void addGidcClinicMarker(@NonNull GoogleMap googleMap) {
        LatLng GidcClinicLocation = new LatLng(22.535638879965898, 72.93080303323642);
        MarkerOptions GidcClinicMarkerOptions = new MarkerOptions().position(GidcClinicLocation)
                .title("Gidc Clinic");
        GidcClinicMarker = googleMap.addMarker(GidcClinicMarkerOptions);
    }
    private void addDrSanjayBhoiMarker(@NonNull GoogleMap googleMap) {
        LatLng DrSanjayBhoiLocation = new LatLng(22.533315776867468, 72.91651485617174);
        MarkerOptions DrSanjayBhoiMarkerOptions = new MarkerOptions().position(DrSanjayBhoiLocation)
                .title("Dr Sanjay Bhoi");
        DrSanjayBhoiMarker = googleMap.addMarker(DrSanjayBhoiMarkerOptions);
    }
    private void addH_M_PatelCenterforMedicalCareandEducationMarker(@NonNull GoogleMap googleMap) {
        LatLng H_M_PatelCenterforMedicalCareandEducationLocation = new LatLng(22.53981858924278, 72.89215609975386);
        MarkerOptions H_M_PatelCenterforMedicalCareandEducationMarkerOptions = new MarkerOptions().position(H_M_PatelCenterforMedicalCareandEducationLocation)
                .title("H.M.Patel Center for Medical Care and Education");
        H_M_PatelCenterforMedicalCareandEducationMarker = googleMap.addMarker(H_M_PatelCenterforMedicalCareandEducationMarkerOptions);
    }
    private void addShreeKrishnaHospitalMarker(@NonNull GoogleMap googleMap) {
        LatLng ShreeKrishnaHospitalLocation = new LatLng(22.539563677123745, 72.89363211288105);
        MarkerOptions ShreeKrishnaHospitalMarkerOptions = new MarkerOptions().position(ShreeKrishnaHospitalLocation)
                .title("Shree Krishna Hospital");
        ShreeKrishnaHospitalMarker = googleMap.addMarker(ShreeKrishnaHospitalMarkerOptions);
    }
    private void addBhanubhaiandMadhubenPatelCardiacCentreMarker(@NonNull GoogleMap googleMap) {
        LatLng BhanubhaiandMadhubenPatelCardiacCentreLocation = new LatLng(22.53984850704999, 72.89415681586947);
        BitmapDescriptor icon = setIcon(Map_Activity.this, R.drawable.hospital_marker);
        MarkerOptions BhanubhaiandMadhubenPatelCardiacCentreMarkerOptions = new MarkerOptions().position(BhanubhaiandMadhubenPatelCardiacCentreLocation)
                .title("Bhanubhai and Madhuben Patel Cardiac Centre")
                .icon(icon);
        BhanubhaiandMadhubenPatelCardiacCentreMarker = googleMap.addMarker(BhanubhaiandMadhubenPatelCardiacCentreMarkerOptions);
    }
    private void redirectToDetailPage(String hospitalName) {
        Intent intent = new Intent(Map_Activity.this, hospital_detail_page.class);
        intent.putExtra("hospitalName", hospitalName);
        startActivity(intent);
    }


//    to add custom icon of hospital
    public BitmapDescriptor setIcon(Activity context, int drawableID){
            Drawable drawable = ActivityCompat.getDrawable(context,drawableID);
            drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }


}