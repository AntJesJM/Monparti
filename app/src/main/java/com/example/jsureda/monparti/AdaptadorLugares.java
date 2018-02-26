package com.example.jsureda.monparti;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

public class AdaptadorLugares extends CursorAdapter {

    public AdaptadorLugares(Context context, Cursor c) {
        super(context, c, 0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.info_lugares, viewGroup, false);
    }

    public void bindView(View view, final Context context, Cursor cursor) {
        TextView txtNombre = (TextView) view.findViewById(R.id.lblNombre);
        TextView txtHorario = (TextView) view.findViewById(R.id.lblHorario);
        final ImageView imgLugar = (ImageView) view.findViewById(R.id.imgLugar);

        String nombre = cursor.getString(cursor.getColumnIndex(TablaLugares.Columna.NOMBRE));
        String horario = cursor.getString(cursor.getColumnIndex(TablaLugares.Columna.HOR));
        String lugarUri = cursor.getString(cursor.getColumnIndex(TablaLugares.Columna.IMAGEN));

        txtNombre.setText(nombre);
        txtHorario.setText(horario);
        Glide.with(context).load(Uri.parse("file:///android_asset/" + lugarUri)).asBitmap().error(R.drawable.logo_monparti_borde).centerCrop().into(new BitmapImageViewTarget(imgLugar) {
            protected void setResource(Bitmap resource){
                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                drawable.setCircular(true);
                imgLugar.setImageDrawable(drawable);
            }
        });
    }
}
