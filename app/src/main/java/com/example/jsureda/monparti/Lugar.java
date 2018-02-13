package com.example.jsureda.monparti;


import android.content.ContentValues;

import java.util.UUID;

public class Lugar {
    private String id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String valoracion;
    private String longitud;
    private String latitud;
    private String imagen;

    public Lugar(String nombre, String descripcion, String categoria, String valoracion, String longitud, String latitud, String imagen){
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.descripcion = descripcion;
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
        valores.put(EstructuraLugares.EntradaLugares.ID,id);
        valores.put(EstructuraLugares.EntradaLugares.NOMBRE,nombre);
        valores.put(EstructuraLugares.EntradaLugares.CATEGORIA,categoria);
        valores.put(EstructuraLugares.EntradaLugares.VALORACION,valoracion);
        valores.put(EstructuraLugares.EntradaLugares.LONGITUD,longitud);
        valores.put(EstructuraLugares.EntradaLugares.LATITUD,latitud);
        valores.put(EstructuraLugares.EntradaLugares.IMAGEN,imagen);

        return valores;
    }
}
