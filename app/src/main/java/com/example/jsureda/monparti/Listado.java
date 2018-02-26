package com.example.jsureda.monparti;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

public class Listado extends AppCompatActivity {

    private LugarDBHelper mLugarDBHelper;

    private ListView mLugarList;
    private AdaptadorLugares mLugarAdapter;

    public Listado(){

    }

    public static Listado newInstance(){
        return new Listado();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLugarList = (ListView) findViewById(R.id.LVSel);
        mLugarList.setAdapter(mLugarAdapter);
        mLugarDBHelper = new LugarDBHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAgregar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent homeIntent = new Intent(Listado.this, IntroducirLugar.class);
                startActivity(homeIntent);
                finish();
            }
        });
    }


    public void verMapa(View v){

        Intent intent = new Intent(Listado.this,VerMapa.class);
        startActivity(intent);
    }



}
