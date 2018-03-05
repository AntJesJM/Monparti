package com.example.jsureda.monparti;


import android.provider.BaseColumns;

public class TablaLugares {
    public static abstract class Columna implements BaseColumns {
        public static final String TABLE_NAME = "lugar";
        public static final String ID = "id";
        public static final String NOMBRE = "nombre";
        public static final String DESC = "descripcion";
        public static final String HOR = "horario";
        public static final String CATEGORIA = "categoria";
        public static final String VALORACION = "valoracion";
        public static final String LONGITUD = "longitud";
        public static final String LATITUD = "latitud";
        public static final String IMAGEN = "imagen";
    }

}
