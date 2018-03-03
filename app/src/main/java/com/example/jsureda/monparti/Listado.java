package com.example.jsureda.monparti;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class Listado extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int REQUEST_UPDATE_DELETE_LUGAR = 2;

    public static Spinner spinner;
    private ListView mLugarList;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;
    public static final String EXTRA_LUGAR_ID = "extra_lugar_id";
    ArrayAdapter<CharSequence> adapter;
    private LugarCursorAdapter mLugarAdapter;
    private LugarDBHelper mLugarDbHelper;
    private FloatingActionButton fab;
    boolean exit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // int permissionCheck = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            }
        }
        spinner = (Spinner) findViewById(R.id.spnCatSel);
        adapter = ArrayAdapter.createFromResource(this, R.array.spinnerCategoria, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        mLugarList = (ListView) findViewById(R.id.listSel);


        fab = (FloatingActionButton) findViewById(R.id.btnAgregar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), IntroducirLugar.class);
                startActivityForResult(intent, 2);
            }
        });
       mLugarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mLugarAdapter.getItem(i);
                String currentLugarID = currentItem.getString(
                        currentItem.getColumnIndex(TablaLugares.Columna.ID));

                mostrarLugar(currentLugarID);
            }
        });
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        mLugarList.setAdapter(null);
        mLugarAdapter = new LugarCursorAdapter(getApplicationContext(), null);
        // Setup
        mLugarList.setAdapter(mLugarAdapter);
        // Instancia de helper
        mLugarDbHelper = new LugarDBHelper(getApplicationContext());
        // Carga de datos
        cargarLugares();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void mostrarLugar(String LugarID) {
        Intent intent = new Intent(getApplicationContext(), VerLugar.class);
        intent.putExtra(EXTRA_LUGAR_ID, LugarID);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_LUGAR);
    }

    public void verMapa(View v) {
        Intent intent = new Intent(Listado.this, VerMapa.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
        startActivity(getIntent());
    }

    private void cargarLugares() {
        new LugarLoadTask().execute();
    }
    private class LugarLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mLugarDbHelper.getAllLugares();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                mLugarAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, R.string.salir,
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

}

