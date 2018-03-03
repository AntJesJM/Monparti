package com.example.jsureda.monparti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class VerMapa extends AppCompatActivity
        implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    Spinner spnCategorias;
    ArrayAdapter<CharSequence> adapter;
    private LugarCursorAdapter mLugarAdapter;
    private LugarDBHelper mLugarDbHelper;
    String catgs;
    String catg[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mapa);
        catg = getResources().getStringArray(R.array.spinnerCategoria);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewMap);
        mapFrag.getMapAsync(this);
        catgs = getIntent().getStringExtra("categorias");
        mLugarDbHelper = new LugarDBHelper((getApplicationContext()));



        spnCategorias = (Spinner) findViewById(R.id.spnCatMap);
        adapter = ArrayAdapter.createFromResource(this, R.array.spinnerCategoria, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategorias.setAdapter(adapter);
        spnCategorias.setOnItemSelectedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMapOptions options = new GoogleMapOptions();
        mGoogleMap = googleMap;
        options.mapType(mGoogleMap.MAP_TYPE_NORMAL).compassEnabled(true).rotateGesturesEnabled(true).tiltGesturesEnabled(true)
        ;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }


                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
               /* MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");*/
                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                //mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));


            }
        }

        ;

    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.tituloPermisosLoc)
                        .setMessage(R.string.mensajePermisosLoc)
                        .setPositiveButton(R.string.botonAceptarPermisos, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(VerMapa.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void CargarLugares (String catgs, GoogleMap mGoogleMap) {

    }
    private void loadLugar() {
        new GetLugares().execute();
    }

    private void showLugar (Lugar lugar) {
        int icons[] = {
                R.drawable.monumentos,
                R.drawable.parques,
                R.drawable.tiendas
        };
        int icono = 0;
        LatLng rest= new LatLng(Double.parseDouble(lugar.getLatitud()), Double.parseDouble(lugar.getLongitud()));
        for(int i=0;i<catg.length;i++)
            if  (lugar.getCategoria().equals(catg[i])) {
                icono = icons[i];
                break;
            }


        int height = 100;
        int width = 100;

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(icono);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        if (catgs.equals("Ver todos")||catgs.equals(lugar.getCategoria())) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(rest)
                    .title(lugar.getNombre())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

        }
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(rest)
                .zoom(11)
                .build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



    private class GetLugares extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mLugarDbHelper.getAllLugares();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                showLugar(new Lugar(cursor));
                cursor.moveToNext();
            }
        }

    }
}