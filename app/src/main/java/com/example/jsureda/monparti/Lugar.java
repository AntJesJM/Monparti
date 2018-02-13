package com.example.jsureda.monparti;


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
}
