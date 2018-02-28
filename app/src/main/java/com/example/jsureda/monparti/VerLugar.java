package com.example.jsureda.monparti;

import android.app.AlertDialog;
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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

public class VerLugar extends AppCompatActivity {

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
    RatingBar bar;
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

        mNombre=(TextView)findViewById(R.id.lblNombreDB);
        mCategoria=(TextView)findViewById(R.id.lblCatDB);
        mDescripcion=(TextView) findViewById(R.id.lblDescDB);
        bar=(RatingBar)findViewById(R.id.barraNotaAned);
        img=(ImageView)findViewById(R.id.imgAned);
        mLugarID = getIntent().getStringExtra(Listado.EXTRA_LUGAR_ID);
        mLugarDBHelper=new LugarDBHelper(getApplicationContext());
        cargarLugar();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng rest = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
        mMap.addMarker(new MarkerOptions().position(rest).title("Lugar " + mNombre.getText()));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(rest)
                .zoom(11)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

    private void cargarLugar() {
        new GetLugarByIDTask().execute();
    }

    private void showRestaurante(Lugar lugar) {

        latitud=lugar.getLatitud();
        longitud=lugar.getLongitud();
        mNombre.setText(lugar.getNombre());
        mCategoria.setText(lugar.getCategoria());
        mDescripcion.setText(lugar.getDescripcion());
        bar.setRating(Float.parseFloat(lugar.getValoracion()));
        foto=lugar.getImagen();
        Bitmap icon=null;
        if(foto.equals(""))
        {
            icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.sinimagen);
        }
        else if(foto.startsWith("/"))
        {
            File imgFile = new File(foto);
            if(imgFile.exists()) {
                icon=BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
            else {
                icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.sinimagen);
            }
        }
        else
        {
            icon=StringBitmap.StringToBitMap(foto);
        }
        img.setImageBitmap(icon);
    }

    private void showLoadError() {
        Toast.makeText(getApplicationContext(),
                "Error al cargar información", Toast.LENGTH_SHORT).show();
    }

    private class GetLugarByIDTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mLugarDBHelper.getLugarPorID(mLugarID);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                showRestaurante(new Lugar(cursor));
            } else {
                showLoadError();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iBtnEdit:
                mostrarEditar();
                break;
            case R.id.iBtnDel:
                /*
		 * Borramos el registro con confirmación
		 */
                AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

                dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
                dialogEliminar.setTitle(getResources().getString(R.string.lugar_eliminar_articulo));
                dialogEliminar.setMessage(getResources().getString(R.string.lugar_eliminar_mensaje));
                dialogEliminar.setCancelable(false);

                dialogEliminar.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int boton) {

				/*
				 * Devolvemos el control
				 *
				 */
                        new DeleteLugarTask().execute();
                        setResult(RESULT_OK);
                        finish();
                    }
                });

                dialogEliminar.setNegativeButton(android.R.string.no, null);

                dialogEliminar.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarEditar() {
        Intent intent = new Intent(getApplicationContext(),IntroducirLugar.class);
        intent.putExtra(EXTRA_LUGAR_ID, mLugarID);
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
        Toast.makeText(getApplicationContext(),R.string.lugar_eliminar_confirmacion, Toast.LENGTH_SHORT).show();
        finish();
    }
    private void showDeleteError() {
        Toast.makeText(getApplicationContext(),
                "Error al eliminar lugar", Toast.LENGTH_SHORT).show();
    }

}
