package com.example.jsureda.monparti;


import android.content.ContentValues;

import java.util.UUID;

public class Lugar {
    private String id;
    private String nombre;
    private String descripcion;
    private String horario;
    private String categoria;
    private String valoracion;
    private String longitud;
    private String latitud;
    private String imagen;

    public Lugar(String nombre, String descripcion, String horario,String categoria, String valoracion, String longitud, String latitud, String imagen){
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.horario = horario;
        this.categoria = categoria;
        this.valoracion = valoracion;
        this.longitud = longitud;
        this.latitud = latitud;
        this.imagen = imagen;
    }
    public String getId(){
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getHorario(){
        return horario;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getValoracion() {
        return valoracion;
    }

    public String getLongitud() {
        return longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getImagen() {
        return imagen;
    }

    public ContentValues toContentValues(){
        ContentValues valores = new ContentValues();
        valores.put(TablaLugares.Columna.ID,id);
        valores.put(TablaLugares.Columna.NOMBRE,nombre);
        valores.put(TablaLugares.Columna.CATEGORIA,categoria);
        valores.put(TablaLugares.Columna.VALORACION,valoracion);
        valores.put(TablaLugares.Columna.LONGITUD,longitud);
        valores.put(TablaLugares.Columna.LATITUD,latitud);
        valores.put(TablaLugares.Columna.IMAGEN,imagen);

        return valores;
    }
}
