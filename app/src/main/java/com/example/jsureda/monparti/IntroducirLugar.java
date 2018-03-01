package com.example.jsureda.monparti;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Calendar;

public class IntroducirLugar extends AppCompatActivity implements OnMapReadyCallback,LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    EditText nombre, descripcion, apertura, cierre;
    Spinner spnCategorias;
    RatingBar barra;
    ImageView iv;
    ImageButton guardar, galeria, camara;
    private static int DESDE_CAMARA = 1;
    private static int DESDE_GALERIA = 2;
    GoogleMap mGoogleMap;
    private static final int MY_PERMISSIONS_REQUEST_GALLERY = 90;
    private static final int MY_PERMISSIONS_CAMERA = 100;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    boolean vacio = false;
    private LugarDBHelper mLugarDbHelper;
    static String imagen = "";
    GoogleMap mMap;
    private double latitud=0;
    private double longitud=0;
    final String TAG = "GPS";
    private long UPDATE_INTERVAL = 2 * 1000;
    private long FASTEST_INTERVAL = 2000;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    GoogleApiClient gac;
    LocationRequest locationRequest;
    boolean editable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducir_lugar);
        inicializarUI();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        editable = getIntent().getBooleanExtra("editando",false);
        isGooglePlayServicesAvailable();
        if(!isLocationEnabled())
            showAlert();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLugarDbHelper = new LugarDBHelper(getApplicationContext());

        MapFragment mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapViewAned, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnConfirmar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                introLugar();
                if (vacio == false) {

                    Intent homeIntent = new Intent(IntroducirLugar.this, Listado.class);
                    startActivity(homeIntent);
                    finish();
                }

            }
        });

        apertura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(IntroducirLugar.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        apertura.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
        });

        cierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(IntroducirLugar.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        cierre.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
        });
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_CAMERA);
                if (ContextCompat.checkSelfPermission(IntroducirLugar.this,
                        Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = null;
                    int code = 0;
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    code = DESDE_CAMARA;
                    startActivityForResult(intent, code);
                } else {
                    return;
                }
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_GALLERY);
                if (ContextCompat.checkSelfPermission(IntroducirLugar.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = null;
                    int code = 0;
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    code = DESDE_GALERIA;
                    startActivityForResult(intent, code);
                } else {
                    return;
                }
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Toast.makeText(IntroducirLugar.this, "Long: " + longitud + ", Lat: " + latitud, Toast.LENGTH_LONG).show();
                }

        });
    }
    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    protected void onStart() {
        gac.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        gac.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            updateUI(location);
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(IntroducirLugar.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
        Log.d(TAG, "onConnected");
        Location ll = LocationServices.FusedLocationApi.getLastLocation(gac);
        Log.d(TAG, "LastLocation: " + (ll == null ? "NO LastLocation" : ll.toString()));
        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try{
                        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
                    } catch (SecurityException e) {
                        Toast.makeText(IntroducirLugar.this, "SecurityException:\n" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(IntroducirLugar.this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(IntroducirLugar.this, "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
        Log.d("DDD", connectionResult.toString());
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        latitud=loc.getLatitude();
        longitud=loc.getLongitude();
        mMap.clear();
        LatLng rest = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(rest).title("Lugar seleccionado"));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(rest)
                .zoom(15)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGooglePlayServicesAvailable() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        Log.d(TAG, "This device is supported.");
        return true;
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void inicializarUI() {
        nombre = (EditText) findViewById(R.id.txtNombreAned);
        descripcion = (EditText) findViewById(R.id.txtDescAned);
        spnCategorias = (Spinner) findViewById(R.id.spnCatAned);
        apertura = (EditText) findViewById(R.id.txtAbreAned);
        cierre = (EditText) findViewById(R.id.txtCierraAned);
        barra = (RatingBar) findViewById(R.id.barraNotaAned);
        guardar = (ImageButton) findViewById(R.id.iBtnGuardar);
        galeria = (ImageButton) findViewById(R.id.iBtnGaleria);
        camara = (ImageButton) findViewById(R.id.iBtnCamara);
        iv = (ImageView) findViewById(R.id.imgAned);
    }

    private void introLugar() {
        vacio = false;
        String name, desc, open, close, horario, categ, nota, lat, lon;
        name = nombre.getText().toString();
        desc = descripcion.getText().toString();
        open = apertura.getText().toString();
        close = cierre.getText().toString();
        categ = String.valueOf(spnCategorias.getSelectedItem());
        nota = String.valueOf(barra.getRating());
        lat = String.valueOf(latitud);
        lon = String.valueOf(longitud);
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(open) ||
                TextUtils.isEmpty(close) || TextUtils.isEmpty(categ) || TextUtils.isEmpty(nota) || TextUtils.isEmpty(lat) || TextUtils.isEmpty(lon)) {
            Toast.makeText(getApplicationContext(),
                    "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
            vacio = true;
        }
        if (vacio) {
            return;
        }
        horario = open + "-" + close;
        Lugar lugar = new Lugar(name, desc, horario, categ, nota, lon, lat, imagen);

        new AddLugarTask().execute(lugar);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
    private class AddLugarTask extends AsyncTask<Lugar, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Lugar... lugar) {
            return mLugarDbHelper.guardarLugar(lugar[0]) > 0;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
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
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap imagen = null;
        if (requestCode == DESDE_CAMARA && resultCode == RESULT_OK && data != null) {
            imagen = (Bitmap) data.getParcelableExtra("data");
            IntroducirLugar.imagen = StringBitmap.BitMapToString(imagen);

        } else if (requestCode == DESDE_GALERIA && resultCode == RESULT_OK && data != null) {
            Uri rutaImagen = data.getData();
            try {
                imagen = MediaStore.Images.Media.getBitmap(getContentResolver(), rutaImagen);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(rutaImagen, projection, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex);
            IntroducirLugar.imagen = picturePath;
            cursor.close();
        } else {
            Toast toast = Toast.makeText(IntroducirLugar.this, R.string.noFoto, Toast.LENGTH_LONG);
        }
        iv.setImageBitmap(imagen);
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(IntroducirLugar.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(IntroducirLugar.this, permission)) {
                ActivityCompat.requestPermissions(IntroducirLugar.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(IntroducirLugar.this, new String[]{permission}, requestCode);
            }
        }
    }
}