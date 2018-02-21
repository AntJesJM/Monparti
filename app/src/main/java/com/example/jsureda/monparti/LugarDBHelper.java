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
        sqLiteDatabase.execSQL("CREATE TABLE "+ TablaLugares.Columna.NOMBRE_TABLA+" ("
                + TablaLugares.Columna._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TablaLugares.Columna.ID+" TEXT NOT NULL,"
                + TablaLugares.Columna.NOMBRE+"TEXT NOT NULL,"
                + TablaLugares.Columna.DESC+" TEXT NOT NULL,"
                + TablaLugares.Columna.HOR+" TEXT NOT NULL,"
                + TablaLugares.Columna.CATEGORIA+" TEXT NOT NULL,"
                + TablaLugares.Columna.VALORACION+" TEXT NOT NULL,"
                + TablaLugares.Columna.LONGITUD+" TEXT NOT NULL,"
                + TablaLugares.Columna.LATITUD+" TEXT NOT NULL,"
                + TablaLugares.Columna.IMAGEN+" TEXT,"
                +"UNIQUE ("+ TablaLugares.Columna.ID+"))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists lugar");



    }

    public long guardarLugar(Lugar lugar){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(TablaLugares.Columna.NOMBRE_TABLA,null,lugar.toContentValues());
    }
}
