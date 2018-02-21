package com.example.jsureda.monparti;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.maps.MapView;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

public class IntroducirLugar extends AppCompatActivity {
    EditText nombre, descripcion, apertura, cierre;
    Spinner categorias;
    RatingBar barra;
    MapView mapa;
    ImageButton guardar, galeria, camara;
    ImageView imagen;
    private static int DESDE_CAMARA = 1;
    private static int DESDE_GALERIA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducir_lugar);
        inicializarUI();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                code = DESDE_GALERIA;
            }
        });
    }

    public void inicializarUI() {
        nombre = (EditText) findViewById(R.id.txtNombreAned);
        descripcion = (EditText) findViewById(R.id.txtDescAned);
        categorias = (Spinner) findViewById(R.id.spnCatAned);
        apertura= (EditText) findViewById(R.id.txtAbreAned);
        cierre= (EditText) findViewById(R.id.txtCierraAned);
        barra = (RatingBar) findViewById(R.id.barraNotaAned);
        guardar = (ImageButton)findViewById(R.id.iBtnGuardar);
        galeria = (ImageButton)findViewById(R.id.iBtnGaleria);
        camara = (ImageButton)findViewById(R.id.iBtnCamara);



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap imagen = null;

        if (requestCode == DESDE_CAMARA) {
            imagen = (Bitmap) data.getParcelableExtra("data");
        }

        if (requestCode == DESDE_GALERIA) {
            Uri rutaImagen = data.getData();
            try {
                imagen = BitmapFactory.decodeStream(new BufferedInputStream(getContentResolver().openInputStream(rutaImagen)));
            } catch (FileNotFoundException e) { }
        }

        ImageView iv = (ImageView) findViewById(R.id.imgAned);
        iv.setImageBitmap(imagen);
    }

}
