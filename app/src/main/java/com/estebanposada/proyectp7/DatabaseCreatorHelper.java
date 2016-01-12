package com.estebanposada.proyectp7;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by usuario on 13/12/2015.
 */
public class DatabaseCreatorHelper extends SQLiteOpenHelper{

    final static String TABLE_NAME = "baseDatos2";
    final static String CREATE_TABLE = "create table "+TABLE_NAME+
            " (id integer primary key autoincrement," +
            " nombreCom text, pass text, correo text, tel text)";
    //Nombre y apellido, pass, email, telefono


    public DatabaseCreatorHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
