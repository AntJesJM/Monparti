package com.example.jsureda.monparti;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.google.android.gms.maps.MapView;

public class IntroducirLugar extends AppCompatActivity {
    EditText nombre, descripcion;
    Spinner categorias;
    RatingBar barra;
    MapView mapa;
    ImageButton guardar, galeria, camara;
    ImageView imagen;

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
    }

    public void inicializarUI() {
        nombre = (EditText) findViewById(R.id.txtNombreAned);
        descripcion = (EditText) findViewById(R.id.txtDescAned);
        categorias = (Spinner) findViewById(R.id.spnCatAned);
        barra = (RatingBar) findViewById(R.id.barraNotaAned);
        guardar = (ImageButton)findViewById(R.id.iBtnGuardar);
        galeria = (ImageButton)findViewById(R.id.iBtnGaleria);
        camara = (ImageButton)findViewById(R.id.iBtnCamara);
        imagen = (ImageView)findViewById(R.id.imgAned);

    }

}
