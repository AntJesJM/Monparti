package com.example.jsureda.monparti;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.Calendar;

public class IntroducirLugar extends AppCompatActivity implements OnMapReadyCallback {
    EditText nombre, descripcion, apertura, cierre;
    Spinner spnCategorias;
    RatingBar barra;
    ImageView iv;
    ImageButton guardar, galeria, camara;
    private static int DESDE_CAMARA = 1;
    private static int DESDE_GALERIA = 2;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFragment;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 80;
    private static final int MY_PERMISSIONS_REQUEST_GALLERY = 90;
    private static final int MY_PERMISSIONS_CAMERA = 100;
    ArrayAdapter<CharSequence> adapter;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    boolean vacio = false;
    double latitud, longitud;
    private LugarDBHelper mLugarDbHelper;
    static String imagen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducir_lugar);
        inicializarUI();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mLugarDbHelper = new LugarDBHelper(getApplicationContext());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapViewAned);
        mapFragment.getMapAsync(this);
        adapter = ArrayAdapter.createFromResource(this, R.array.spinnerCategoria, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategorias.setAdapter(adapter);
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

                latitud = mLastLocation.getLatitude();
                longitud = mLastLocation.getLongitude();
                if (mLastLocation != null) {
                    Toast.makeText(IntroducirLugar.this, "Long: " + mLastLocation.getLongitude() + ", Lat: " + mLastLocation.getLatitude(), Toast.LENGTH_LONG).show();
                }
            }
        });
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

    private boolean camposVacios(String dato) {
        Toast.makeText(getApplicationContext(),
                "Rellene el campo " + dato, Toast.LENGTH_SHORT).show();
        vacio = true;
        return vacio;
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

    private class AddLugarTask extends AsyncTask<Lugar, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Lugar... lugar) {

            return mLugarDbHelper.guardarLugar(lugar[0]) > 0;


        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                // Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
/*                //Place current location marker

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(R.string.posicion);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);*/

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
            String picturePath = cursor.getString(columnIndex); // returns null
            IntroducirLugar.imagen = picturePath;
            cursor.close();

        } else {
            Toast toast = Toast.makeText(IntroducirLugar.this, R.string.noFoto, Toast.LENGTH_LONG);
        }

        iv.setImageBitmap(imagen);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted, yay! Do the
                        // location-related task you need to do.
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            mGoogleMap.setMyLocationEnabled(true);
                        }

                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(this, R.string.pDenegado, Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            }

            case MY_PERMISSIONS_REQUEST_GALLERY: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do the
                    // calendar task you need to do.
                } else {
                }
            }
            case MY_PERMISSIONS_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do the
                    // calendar task you need to do.
                } else {
                }
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.tituloPermisosLoc)
                        .setMessage(R.string.mensajePermisosLoc)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(IntroducirLugar.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(IntroducirLugar.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(IntroducirLugar.this, permission)) {
                ActivityCompat.requestPermissions(IntroducirLugar.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(IntroducirLugar.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_LOCATION);
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
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

}