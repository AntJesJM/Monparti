package com.example.jsureda.monparti;


import android.provider.BaseColumns;

public class EstructuraLugares {
    public static abstract class EntradaLugares implements BaseColumns{
        public static final String NOMBRE_TABLA = "lugar";
        public static final String ID = "id";
        public static final String NOMBRE = "nombre";
        public static final String DESC = "descripcion";
        public static final String CATEGORIA = "categoria";
        public static final String VALORACION = "valoracion";
        public static final String LONGITUD = "longitud";
        public static final String LATITUD = "latitud";
        public static final String IMAGEN = "imagen";
    }

}
