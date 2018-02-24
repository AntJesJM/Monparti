package com.example.jsureda.monparti;

import android.content.Context;
import android.database.Cursor;
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
       sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS " + TablaLugares.Columna.NOMBRE_TABLA);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertarLugar(Lugar lugar){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(TablaLugares.Columna.NOMBRE_TABLA,null,lugar.toContentValues());
    }

    public Cursor getAllLugares(){
        return getReadableDatabase().query(TablaLugares.Columna.NOMBRE_TABLA,null,null,null,null,null,null);
    }
    public Cursor leerPorID(String LugarID){
        Cursor c = getReadableDatabase().query(TablaLugares.Columna.NOMBRE_TABLA,null,TablaLugares.Columna.ID+ " LIKE ?",new String[]{LugarID},null,null,null);
        return c;
    }
}
