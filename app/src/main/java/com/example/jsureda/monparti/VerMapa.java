package com.example.jsureda.monparti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ROSE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;

public class VerMapa extends AppCompatActivity
        implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    public static Spinner spnCategorias;
    ArrayAdapter<CharSequence> adapter;
    private LugarDBHelper mLugarDBHelper;
    String[] categoria = {"Monumentos", "Parques", "Tiendas"};
    float[] marcas = {HUE_YELLOW, HUE_GREEN, HUE_ROSE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mapa);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewMap);
        mapFrag.getMapAsync(this);
        spnCategorias = (Spinner) findViewById(R.id.spnCatMap);
        adapter = ArrayAdapter.createFromResource(this, R.array.spinnerCategoria, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategorias.setAdapter(adapter);
        spnCategorias.setOnItemSelectedListener(this);
        mLugarDBHelper = new LugarDBHelper(getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();


        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMapOptions options = new GoogleMapOptions();
        mGoogleMap = googleMap;
        options.mapType(mGoogleMap.MAP_TYPE_NORMAL).compassEnabled(true).rotateGesturesEnabled(true).tiltGesturesEnabled(true);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000);
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {

                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
        cargarLugares();
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }


                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.tituloPermisosLoc)
                        .setMessage(R.string.mensajePermisosLoc)
                        .setPositiveButton(R.string.botonAceptarPermisos, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ActivityCompat.requestPermissions(VerMapa.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {


                    Toast.makeText(this, R.string.pDenegado, Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mGoogleMap.clear();
        mLugarDBHelper = new LugarDBHelper(getApplicationContext());

        cargarLugares();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void cargarLugares() {
        new VerMapa.LugarLoadTask().execute();
    }

    private class LugarLoadTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            return mLugarDBHelper.getAllLugaresMapa();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                showLugar(new Lugar(cursor));
                cursor.moveToNext();
            }
        }
    }

    private void showLugar(Lugar lugar) {
        float marca = 0;
        LatLng rest = new LatLng(Double.parseDouble(lugar.getLatitud()), Double.parseDouble(lugar.getLongitud()));
        for (int i = 0; i < categoria.length; i++)
            if (lugar.getCategoria().equals(categoria[i])) {
                marca = marcas[i];
                break;
            }



        mGoogleMap.addMarker(new MarkerOptions()
                .position(rest)
                .title(lugar.getNombre())
                .icon(BitmapDescriptorFactory.defaultMarker(marca)));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(rest)
                .zoom(14)
                .build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}