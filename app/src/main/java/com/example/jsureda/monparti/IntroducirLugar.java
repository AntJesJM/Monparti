package com.example.jsureda.monparti;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

public class IntroducirLugar extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    EditText nombre, descripcion, apertura, cierre;
    Spinner categorias;
    RatingBar barra;
    ImageButton guardar, galeria, camara;
    private static int DESDE_CAMARA = 1;
    private static int DESDE_GALERIA = 2;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    double latitude ;
    double longitude ;
    Criteria criteria ;
    String bestProvider;
    Location location ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducir_lugar);
        inicializarUI();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(IntroducirLugar.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(IntroducirLugar.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, (LocationListener) IntroducirLugar.this);
        criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(bestProvider);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapViewAned);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnConfirmar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent homeIntent = new Intent(IntroducirLugar.this, Listado.class);
                startActivity(homeIntent);
                finish();
            }
        });

        // perform click event listener on edit text
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
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        // perform click event listener on edit text
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
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                int code = 0;
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                code = DESDE_CAMARA;
                startActivityForResult(intent, code);
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                int code = 0;
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                code = DESDE_GALERIA;
                startActivityForResult(intent, code);
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (location == null) {
                    Toast.makeText(getApplicationContext(), "GPS signal not found", Toast.LENGTH_SHORT).show();
                }
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    LatLng posicion = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(posicion)
                            .title("Posición"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion));
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    onLocationChanged(location);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMinZoomPreference(15.0f);
        mMap.setMaxZoomPreference(20.0f);
        LatLng posicionInicial = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(posicionInicial)
                .title("Posición"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicionInicial));
    }

    public void inicializarUI() {
        nombre = (EditText) findViewById(R.id.txtNombreAned);
        descripcion = (EditText) findViewById(R.id.txtDescAned);
        categorias = (Spinner) findViewById(R.id.spnCatAned);
        apertura = (EditText) findViewById(R.id.txtAbreAned);
        cierre = (EditText) findViewById(R.id.txtCierraAned);
        barra = (RatingBar) findViewById(R.id.barraNotaAned);
        guardar = (ImageButton) findViewById(R.id.iBtnGuardar);
        galeria = (ImageButton) findViewById(R.id.iBtnGaleria);
        camara = (ImageButton) findViewById(R.id.iBtnCamara);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap imagen = null;
        if (requestCode == DESDE_CAMARA && resultCode == RESULT_OK && data != null) {
            imagen = (Bitmap) data.getParcelableExtra("data");
        } else if (requestCode == DESDE_GALERIA && resultCode == RESULT_OK && data != null) {
            Uri rutaImagen = data.getData();
            try {
                imagen = BitmapFactory.decodeStream(new BufferedInputStream(getContentResolver().openInputStream(rutaImagen)));
            } catch (FileNotFoundException e) {
            }
        } else {
            Toast toast = Toast.makeText(IntroducirLugar.this, "No hay fotos", Toast.LENGTH_LONG);
        }
        ImageView iv = (ImageView) findViewById(R.id.imgAned);
        iv.setImageBitmap(imagen);


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
