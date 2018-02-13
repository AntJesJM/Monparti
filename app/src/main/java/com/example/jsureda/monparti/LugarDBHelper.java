package com.example.jsureda.monparti;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class LugarDBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Lugares.db";
    public LugarDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ EstructuraLugares.EntradaLugares.NOMBRE_TABLA+" ("
                + EstructuraLugares.EntradaLugares._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EstructuraLugares.EntradaLugares.ID+" TEXT NOT NULL,"
                + EstructuraLugares.EntradaLugares.NOMBRE+"TEXT NOT NULL,"
                + EstructuraLugares.EntradaLugares.DESC+" TEXT NOT NULL,"
                + EstructuraLugares.EntradaLugares.CATEGORIA+" TEXT NOT NULL,"
                + EstructuraLugares.EntradaLugares.VALORACION+" TEXT NOT NULL,"
                + EstructuraLugares.EntradaLugares.LONGITUD+" TEXT NOT NULL,"
                + EstructuraLugares.EntradaLugares.LATITUD+" TEXT NOT NULL,"
                + EstructuraLugares.EntradaLugares.IMAGEN+" TEXT,"
                +"UNIQUE ("+ EstructuraLugares.EntradaLugares.ID+"))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long guardarLugar(Lugar lugar){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(EstructuraLugares.EntradaLugares.NOMBRE_TABLA,null,lugar.toContentValues());
    }
}
