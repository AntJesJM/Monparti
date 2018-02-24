package com.example.jsureda.monparti;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AdaptadorLugares extends ArrayAdapter{
    Activity act;
    Lugar[] info;


    AdaptadorLugares(Activity act,Lugar[] info){
        super(act,R.layout.info_lugares);
        this.act = act;
        this.info = info;

    }

    public View getView(int position, View convertView, ViewGroup parent){
        View item = convertView;
        ListadoHolder holder;

        if(item == null){
            LayoutInflater inflater = act.getLayoutInflater();

            item = inflater.inflate(R.layout.info_lugares,null);
            holder = new ListadoHolder();
            holder.nombre = (TextView) item.findViewById(R.id.txtNombreAned);
            holder.horario = (TextView) item.findViewById(R.id.txtAbreAned);
            holder.nota = (RatingBar) item.findViewById(R.id.ratBarSel);
            holder.foto = (ImageView) item.findViewById(R.id.imgLugar);
        }else{
            holder = (ListadoHolder) item.getTag();
        }
        holder.nombre.setText(info[position].getNombre());
        holder.horario.setText(info[position].getHorario());
        holder.nota.setRating(Float.parseFloat(info[position].getValoracion()));
        //holder seteando valor ratingbar
        holder.foto.setImageResource(Integer.parseInt(info[position].getImagen()));
        return(item);
    }
}
