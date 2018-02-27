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
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TablaLugares.Columna.NOMBRE_TABLA+" ("
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

        mockData(db);

    }

    private void mockData(SQLiteDatabase database){
        mockLugar(database, new Lugar("prueba","esto es una prueba","12:00-20:00","monumento","3.5","12","12","logo_monparti_borde.png"));
    }
    public long mockLugar(SQLiteDatabase db, Lugar lugar) {
        return db.insert(
                TablaLugares.Columna.NOMBRE_TABLA,
                null,
                lugar.toContentValues());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int antigua, int nueva) {

    }

    public long GuardarLugar(Lugar lugar) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                TablaLugares.Columna.NOMBRE_TABLA,null,lugar.toContentValues());
    }

    public Cursor getAllLugares() {
        return getReadableDatabase()
                .query(
                        TablaLugares.Columna.NOMBRE_TABLA,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getLugarporID(String lugarID) {
        Cursor c = getReadableDatabase().query(
                TablaLugares.Columna.NOMBRE_TABLA,
                null,
                TablaLugares.Columna.ID + " LIKE ?",
                new String[]{lugarID},
                null,
                null,
                null);
        return c;
    }

    public int borrarLugar(String lugarID) {
        return getWritableDatabase().delete(
                TablaLugares.Columna.NOMBRE_TABLA,
                TablaLugares.Columna.ID + " LIKE ?",
                new String[]{lugarID});
    }

    public int updateLugar(Lugar lugar, String lugarID) {
        return getWritableDatabase().update(
                TablaLugares.Columna.NOMBRE_TABLA,
                lugar.toContentValues(),
                TablaLugares.Columna.ID + " LIKE ?",
                new String[]{lugarID}
        );
    }

}
