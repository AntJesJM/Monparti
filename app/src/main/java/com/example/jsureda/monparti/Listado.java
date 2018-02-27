package com.example.jsureda.monparti;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

public class Listado extends AppCompatActivity {

    public static final int REQUEST_UPDATE_DELETE_LAWYER = 2;

    private LugarDBHelper mLugarDBHelper;

    private ListView mLugarList;
    private AdaptadorLugares mLugarAdapter;


    public Listado() {
        // Required empty public constructor
    }

    public static Listado newInstance() {
        return new Listado();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLugarList = (ListView) findViewById(R.id.LVSel);
        mLugarAdapter = new AdaptadorLugares(this,null);

        mLugarList.setAdapter(mLugarAdapter);

        this.deleteDatabase(LugarDBHelper.DATABASE_NAME);

        // Instancia de helper
        mLugarDBHelper = new LugarDBHelper(this);

        // Carga de datos
        cargarLugares();

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

    private class CargarLugar extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mLugarDBHelper.getAllLugares();
        }

        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                mLugarAdapter.swapCursor(cursor);
            }
        }
    }

    private void cargarLugares() {
        new CargarLugar().execute();
    }


    public void verMapa(View v) {

        Intent intent = new Intent(Listado.this, VerMapa.class);
        startActivity(intent);
    }


}

