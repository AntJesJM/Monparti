package com.example.jsureda.monparti;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

public class VerLugar extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    private String mLugarID;
    public static final String EXTRA_LUGAR_ID = "extra_lugar_id";
    private TextView mNombre;
    private TextView mCategoria;
    private TextView mDescripcion;
    private TextView mHorario;
    private String longitud;
    private String latitud;
    private String foto;
    private RatingBar bar;
    private ImageButton editar;
    private ImageButton borrar;
    private LocationListener listener;
    private LocationManager locationManager;
    private ImageView img;
    private LugarDBHelper mLugarDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_lugar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNombre = (TextView) findViewById(R.id.lblNombreDB);
        mCategoria = (TextView) findViewById(R.id.lblCatDB);
        mDescripcion = (TextView) findViewById(R.id.lblDescDB);
        bar = (RatingBar) findViewById(R.id.barraNotaVer);
        mHorario = (TextView) findViewById(R.id.lblHorDB2);
        img = (ImageView) findViewById(R.id.imgVer);
        mLugarID = getIntent().getStringExtra(Listado.EXTRA_LUGAR_ID);
        mLugarDBHelper = new LugarDBHelper(getApplicationContext());

        cargarLugar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Listado.class);
                startActivityForResult(intent, 2);
            }
        });

        editar = (ImageButton) findViewById(R.id.iBtnEdit);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarEditar();

            }
        });

        borrar = (ImageButton) findViewById(R.id.iBtnDel);
        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(VerLugar.this);

                dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
                dialogEliminar.setTitle(getResources().getString(R.string.eliminarLugar));
                dialogEliminar.setMessage(getResources().getString(R.string.mensajeEliminar));
                dialogEliminar.setCancelable(false);

                dialogEliminar.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int boton) {
                        new DeleteLugarTask().execute();
                        setResult(RESULT_OK);
                        finish();
                    }
                });

                dialogEliminar.setNegativeButton(android.R.string.no, null);

                dialogEliminar.show();
            }
        });

        MapFragment mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapViewVer, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng rest = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
        mMap.addMarker(new MarkerOptions().position(rest).title("Lugar " + mNombre.getText()));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(rest)
                .zoom(15)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

    private void cargarLugar() {
        new GetLugarByIDTask().execute();
    }

    private void mostrarLugar(Lugar lugar) {

        latitud = lugar.getLatitud();
        longitud = lugar.getLongitud();
        mNombre.setText(lugar.getNombre());
        mCategoria.setText(lugar.getCategoria());
        mDescripcion.setText(lugar.getDescripcion());
        mHorario.setText(lugar.getHorario());
        bar.setRating(Float.parseFloat(lugar.getValoracion()));
        foto = lugar.getImagen();
        Bitmap icon = null;
        if (foto.equals("")) {
            icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.sinimagen);
        } else if (foto.startsWith("/")) {
            File imgFile = new File(foto);
            if (imgFile.exists()) {
                icon = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            } else {
                icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.sinimagen);
            }
        } else {
            icon = StringBitmap.StringToBitMap(foto);
        }
        img.setImageBitmap(icon);
    }

    private void showLoadError() {
        Toast.makeText(getApplicationContext(),
                R.string.errorCargarInfo, Toast.LENGTH_SHORT).show();
    }

    private class GetLugarByIDTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mLugarDBHelper.getLugarPorID(mLugarID);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                mostrarLugar(new Lugar(cursor));
            } else {
                showLoadError();
            }
        }
    }

    private void mostrarEditar() {
        Intent intent = new Intent(getApplicationContext(), IntroducirLugar.class);
        intent.putExtra(EXTRA_LUGAR_ID, mLugarID);
        intent.putExtra("editando", true);
        startActivityForResult(intent, Listado.REQUEST_UPDATE_DELETE_LUGAR);
    }

    private class DeleteLugarTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return mLugarDBHelper.borrarLugar(mLugarID);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mostrarLugares(integer > 0);
        }

    }

    private void mostrarLugares(boolean requery) {
        if (!requery) {
            showDeleteError();
        }
        Toast.makeText(getApplicationContext(), R.string.lugarBorradoConfirm, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showDeleteError() {
        Toast.makeText(getApplicationContext(),
                R.string.eliminarLugar, Toast.LENGTH_SHORT).show();
    }

}
