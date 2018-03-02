package com.example.jsureda.monparti;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jsureda.monparti.TablaLugares.Columna;

public class LugarDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Lugares.db";

    public LugarDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Columna.TABLE_NAME + " ("
                + Columna._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Columna.ID + " TEXT NOT NULL,"
                + Columna.NOMBRE + " TEXT NOT NULL,"
                + Columna.DESC + " TEXT,"
                + Columna.HOR + " TEXT,"
                + Columna.CATEGORIA + " TEXT,"
                + Columna.VALORACION + " TEXT,"
                + Columna.LONGITUD + " TEXT,"
                + Columna.LATITUD + " TEXT,"
                + Columna.IMAGEN + " TEXT,"
                + "UNIQUE (" + Columna.ID + "))");

        mockData(db);
    }

    private void mockData(SQLiteDatabase database) {
        mockLugar(database, new Lugar("Plaza Pistacho", "Es un monumento", "12:00-20:00", "Monumentos", "4.5", "37.404661", "-5.994504", ""));
        mockLugar(database, new Lugar("Tienda Pocha", "Es una tienda", "12:00-20:00", "Tiendas", "3.5", "37.404661", "-5.994504", ""));
        mockLugar(database,new Lugar("Cojones","Puta Mierda","10:00","Parques","5","-7.404661","37.404661",""));
    }

    public long mockLugar(SQLiteDatabase db, Lugar lugar) {
        return db.insert(
                Columna.TABLE_NAME,
                null,
                lugar.toContentValues());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int antigua, int nueva) {

    }

    public long guardarLugar(Lugar lugar) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                TablaLugares.Columna.TABLE_NAME, null, lugar.toContentValues());
    }

    public Cursor getAllLugares() {

        if (Listado.spinner.getSelectedItem().equals("Elegir categoría")) {
            return getReadableDatabase()
                    .query(
                            TablaLugares.Columna.TABLE_NAME,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
        } else {
            String categoria = (String) Listado.spinner.getSelectedItem();
            return getReadableDatabase().query(
                    TablaLugares.Columna.TABLE_NAME,
                    null,
                    TablaLugares.Columna.CATEGORIA + " LIKE ?",
                    new String[]{categoria},
                    null,
                    null,
                    null);
        }
    }

    public Cursor getLugarPorID(String lugarID) {
        Cursor c = getReadableDatabase().query(
                TablaLugares.Columna.TABLE_NAME,
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
                TablaLugares.Columna.TABLE_NAME,
                TablaLugares.Columna.ID + " LIKE ?",
                new String[]{lugarID});
    }

    public int updateLugar(Lugar lugar, String lugarID) {
        return getWritableDatabase().update(
                TablaLugares.Columna.TABLE_NAME,
                lugar.toContentValues(),
                TablaLugares.Columna.ID + " LIKE ?",
                new String[]{lugarID}
        );
    }

}
