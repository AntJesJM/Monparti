package com.example.jsureda.monparti;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.io.File;

public class LugarCursorAdapter extends CursorAdapter {

    public LugarCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.info_lugar, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Referencias UI.
        TextView nombreTxt = (TextView) view.findViewById(R.id.lblNombre);
        TextView horarioTxt = (TextView) view.findViewById(R.id.lblHorario);
        RatingBar rate = (RatingBar) view.findViewById(R.id.ratBarSel);
        ImageView imagen = (ImageView) view.findViewById(R.id.imgLugar);

        // Get valores.
        String name = cursor.getString(cursor.getColumnIndex(TablaLugares.Columna.NOMBRE));
        String horario = cursor.getString(cursor.getColumnIndex(TablaLugares.Columna.HOR));
        Float nota = Float.parseFloat(cursor.getString(cursor.getColumnIndex(TablaLugares.Columna.VALORACION)));
        String imag = cursor.getString(cursor.getColumnIndex(TablaLugares.Columna.IMAGEN));

        // Setup.
        nombreTxt.setText(name);
        horarioTxt.setText(horario);
        rate.setRating(nota);
        Bitmap icon=null;
        if(imag.equals(""))
        {
            icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sinimagen);
        }
        else if(imag.startsWith("/"))
        {
            File imgFile = new File(imag);
            if(imgFile.exists()) {
                icon=BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
            else {
                icon = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.sinimagen);
            }
        }
        else
        {
            icon=StringBitmap.StringToBitMap(imag);
        }
        imagen.setImageBitmap(icon);
       /* Glide
                .with(context)
                .load(Uri.parse("file:///android_asset/" + imag))
                .asBitmap()
                .error(R.drawable.logo_monparti_borde)
                .centerCrop()
                .into(new BitmapImageViewTarget(imagen) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable
                                = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        drawable.setCircular(true);
                        imagen.setImageDrawable(drawable);
                    }
                });*/

    }

}
