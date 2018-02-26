package com.example.jsureda.monparti;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

public class IntroducirLugar extends AppCompatActivity implements OnMapReadyCallback {
    EditText nombre, descripcion, apertura, cierre;
    Spinner categorias;
    RatingBar barra;
    ImageButton guardar, galeria, camara;
    private static int DESDE_CAMARA = 1;
    private static int DESDE_GALERIA = 2;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFragment;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 80;
    private static final int MY_PERMISSIONS_REQUEST_GALLERY = 90;
    private static final int MY_PERMISSIONS_CAMERA = 100;
    Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducir_lugar);
        inicializarUI();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                loc = mGoogleMap.getMyLocation();
                if (loc != null) {
                    Toast.makeText(IntroducirLugar.this, "Long: " + loc.getLongitude() + ", Lat: " + loc.getLatitude(), Toast.LENGTH_LONG).show();
                }
            }
        });
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                    mGoogleMap.setMyLocationEnabled(true);
                } else {
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
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_LOCATION);
        mGoogleMap = googleMap;
        loc = mGoogleMap.getMyLocation();
    }

}