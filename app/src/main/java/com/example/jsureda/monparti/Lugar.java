package com.example.jsureda.monparti;


import android.content.ContentValues;
import android.database.Cursor;

import com.example.jsureda.monparti.TablaLugares.Columna;

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

    public Lugar(String nombre, String descripcion, String horario, String categoria, String valoracion, String longitud, String latitud, String imagen) {
        id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.horario = horario;
        this.categoria = categoria;
        this.valoracion = valoracion;
        this.longitud = longitud;
        this.latitud = latitud;
        this.imagen = imagen;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }


    public ContentValues toContentValues() {
        ContentValues valores = new ContentValues();
        valores.put(Columna.ID, id);
        valores.put(Columna.NOMBRE, nombre);
        valores.put(Columna.DESC, descripcion);
        valores.put(Columna.HOR, horario);
        valores.put(Columna.CATEGORIA, categoria);
        valores.put(Columna.VALORACION, valoracion);
        valores.put(Columna.LONGITUD, longitud);
        valores.put(Columna.LATITUD, latitud);
        valores.put(Columna.IMAGEN, imagen);
        return valores;

    }

    public Lugar(Cursor cursor) {
        id = cursor.getString(cursor.getColumnIndex(Columna.ID));
        nombre = cursor.getString(cursor.getColumnIndex(Columna.NOMBRE));
        descripcion = cursor.getString(cursor.getColumnIndex(Columna.DESC));
        horario = cursor.getString(cursor.getColumnIndex(Columna.HOR));
        categoria = cursor.getString(cursor.getColumnIndex(Columna.CATEGORIA));
        valoracion = cursor.getString(cursor.getColumnIndex(Columna.VALORACION));
        longitud = cursor.getString(cursor.getColumnIndex(Columna.LONGITUD));
        latitud = cursor.getString(cursor.getColumnIndex(Columna.LATITUD));
        imagen = cursor.getString(cursor.getColumnIndex(Columna.IMAGEN));
    }

    @Override
    public String toString() {
        return "Lugar{" +
                "ID='" + id + '\'' +
                ", Nombre='" + nombre + '\'' +
                ", Descripcion='" + descripcion + '\'' +
                ", Horario='" + horario + '\'' +
                ", Categoria='" + categoria + '\'' +
                ", Valoracion='" + valoracion + '\'' +
                ", Longitud='" + longitud + '\'' +
                ", Latitud='" + latitud + '\'' +
                ", Foto='" + imagen + '\'' +
                '}';
    }


}
