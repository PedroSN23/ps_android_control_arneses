package com.example.ps_android_control_arneses.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ConexionBd extends SQLiteOpenHelper {
    public ConexionBd(Context context) {
        super(context, "configura.db", null, 10);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String cadena = "create table if not exists configuracion (_id integer primary key autoincrement, archivo_in_name text not null, archivo_in_path text not null, prefijo_out text not null, fecha integer not null, result integer not null);";
        String usuarios = "create table if not exists usuarios (_id integer primary key autoincrement, usuario text not null, password text not null, rol integer not null);";
        String cambios = "create table if not exists cambios (num_tag text not null, indice_data integer not null, valor text not null);";
        String rfid = "create table if not exists rfid (_id integer primary key autoincrement, potencia integer not null)";
        String inventario = "create table if not exists inventario (tag text primary key , fecha_alta text not null, fecha_ultima_lectura text not null, estado text default 0);";
        String movimientos = "create table if not exists movimientos (id integer not null primary key autoincrement, tag text not null, estado integer not null, fecha text not null);";
        db.execSQL(cadena);
        db.execSQL(rfid);
        db.execSQL(usuarios);
        db.execSQL(cambios);
        db.execSQL(inventario);
        db.execSQL(movimientos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String cadenaUpdate = "drop table if exists configuracion;";
        String rfidUpdate = "drop table if exists rfid;";
        String usuariosUpdate = "drop table if exists usuarios;";
        String cambiosUpdate = "drop table if exists cambios;";
        String inventarioUpdate = "drop table if exists inventario;";
        db.execSQL(cadenaUpdate);
        db.execSQL(rfidUpdate);
        db.execSQL(usuariosUpdate);
        db.execSQL(cambiosUpdate);
        db.execSQL(inventarioUpdate);
        onCreate(db);
    }
}