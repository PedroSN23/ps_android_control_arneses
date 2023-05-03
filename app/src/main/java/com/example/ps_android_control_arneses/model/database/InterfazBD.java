package com.example.ps_android_control_arneses.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Base64;

import com.example.ps_android_control_arneses.controller.rfid.TagsRead;
import com.example.ps_android_control_arneses.model.clases.Configuracion;
import com.example.ps_android_control_arneses.model.clases.Usuarios;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;


public class InterfazBD {
    private static final String TAG = "InterfazBD";
    private final ConexionBd con;
    private SQLiteDatabase db;

    public InterfazBD(Context context) {
        con = new ConexionBd(context);
        inicializaDB();
    }

    public void open() throws SQLiteException {
        db = con.getWritableDatabase();
    }

    public void close() throws SQLiteException {
        con.close();
    }

    public void inicializaDB() {
        int numRegistros;
        ContentValues content;
        open();
        numRegistros = traeRegistro("configuracion");
        if (numRegistros == 0) {
            content = new ContentValues();
            content.put("archivo_in_path", "");
            content.put("archivo_in_name", "");
            content.put("prefijo_out", "salida");
            content.put("fecha", 0);
            content.put("result", 0);
            db.insert("configuracion", null, content);
        }

        numRegistros = traeRegistro("usuarios");
        if (numRegistros != 3) {
            truncarTabla("usuarios");
            insertarUsuariosSimple("root", "C24AB9EvcpHrccMN8Muc6spCkatoy0I4RFwvOH/6n0A=", 0);
            insertarUsuariosSimple("admin", "astlix", 1);
            insertarUsuariosSimple("1", "A6xnQhbz4Vx2HuGl4lXwZ5U2I8iziLRFnhP5eNfIRvQ=", 2);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void truncarTabla(String tabla) {
        String query = "DELETE FROM " + tabla + ";";
        db.execSQL(query);
        String query2 = "delete from sqlite_sequence where name='" + tabla + "';";
        db.execSQL(query2);
    }

    public int traeRegistro(String tabla) {
        int ret;
        String consulta = "select _id from " + tabla + ";";
        Cursor c = db.rawQuery(consulta, null);
        ret = c.getCount();
        c.close();
        return ret;
    }

    public Configuracion obtenerConfiguracion() {
        Configuracion configuracion = null;
        open();

        String consulta = "select archivo_in_name, archivo_in_path, prefijo_out, fecha, result from configuracion where _id = 1;";
        try {
            Cursor c = db.rawQuery(consulta, null);
            c.moveToFirst();
            if (c.getCount() != 0) {
                configuracion = new Configuracion(c.getInt(4), c.getInt(3), c.getString(0), c.getString(1), c.getString(2));
            }
            c.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return configuracion;
    }

    public long modificarConfiguracion(Configuracion configuracion) {
        ContentValues content;
        open();
        content = new ContentValues();
        content.put("archivo_in_path", configuracion.getArchivoInPath());
        content.put("archivo_in_name", configuracion.getArchivoInName());
        content.put("prefijo_out", configuracion.getPrefijoOut());
        content.put("fecha", (configuracion.isFecha()) ? 1 : 0);
        content.put("result", (configuracion.isResult()) ? 1 : 0);
        return db.update("configuracion", content, "_id=1", null);
    }


    /*********************USUARIOS*************************/
    private void insertarUsuariosSimple(String usuario, String password, int rol) {
        ContentValues content;
        open();
        content = new ContentValues();
        try {
            content.put("usuario", usuario);
            content.put("password", password);
            content.put("rol", rol);
            db.insert("usuarios", null, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean iniciarSesion(Usuarios usuario) {
        open();
        boolean res = false;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(usuario.getContrasena().getBytes(Charset.forName("UTF-8")));
            String pas64 = Base64.encodeToString(hash, Base64.NO_WRAP);
            String consulta = "select _id, rol from usuarios where usuario = '" + usuario.getUsuario() + "' and password = '" + pas64 + "';";
            Cursor c = db.rawQuery(consulta, null);
            c.moveToFirst();
            if (c.getCount() != 0) {
                usuario.setId(c.getInt(0));
                usuario.setRol(c.getInt(1));
                res = true;
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public void actualizarUsuairo(int id, String user, String pass) {
        ContentValues content;
        open();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pass.getBytes(Charset.forName("UTF-8")));
            String pas64 = Base64.encodeToString(hash, Base64.NO_WRAP);
            content = new ContentValues();
            content.put("usuario", user);
            content.put("password", pas64);
            db.update("usuarios", content, "_id=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> obtenerUsuarios() {
        ArrayList<String> usuariosAL = new ArrayList<>();
        open();
        String consulta = "select usuario from usuarios where _id>1;";
        try {
            Cursor c = db.rawQuery(consulta, null);
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    usuariosAL.add(c.getString(0));
                    c.moveToNext();
                }
            }
            c.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return usuariosAL;
    }

    /********************************INVENTARIO*****************************/
    public ArrayList<String[]> obtenerInventario() {
        ArrayList<String[]> inventario = new ArrayList<>();
        open();
        String query = "SELECT tag, fecha_alta, fecha_ultima_lectura, estado FROM inventario;";
        try {
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    String[] datos = new String[4];
                    datos[0] = c.getString(0);
                    datos[1] = c.getString(1);
                    datos[2] = c.getString(2);
                    datos[3] = c.getString(3);
                    inventario.add(datos);
                    c.moveToNext();
                }
            }
            c.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return inventario;
    }

    public boolean insertarInventario(ArrayList<TagsRead> tagsReadArr) {
        ContentValues values;
        open();
        try {
            for (TagsRead tag : tagsReadArr) {
                values = new ContentValues();
                values.put("tag", tag.getEpc());
                values.put("fecha_alta", tag.getFechaAlta());
                values.put("fecha_ultima_lectura", tag.getFechaUltimaLectura());
                values.put("estado", tag.getEstado());
                db.insertWithOnConflict("inventario", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                // actualizar estado a 1
                values.clear();
                values.put("tag", tag.getEpc());
                values.put("fecha", tag.getFechaUltimaLectura());
                values.put("estado", tag.getEstado());
                db.insertWithOnConflict("movimientos", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }

  public boolean actualizarEstado(ArrayList<TagsRead> tagsReadsArr){
        ContentValues values;
        open();
        try{
            for(TagsRead tag: tagsReadsArr){
                values = new ContentValues();
                values.put("fecha_ultima_lectura", tag.getFechaUltimaLectura());
                values.put("estado", tag.getEstado());
                db.update("inventario", values, "tag = ?", new String[] {tag.getEpc()});
                values.clear();
                values.put("tag", tag.getEpc());
                values.put("fecha", tag.getFechaUltimaLectura());
                values.put("estado", tag.getEstado());
                db.insertWithOnConflict("movimientos", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return  false;
        } finally {
            close();
        }
     }

    public boolean actualizarEstadoDevolucion(ArrayList<TagsRead> tagsReadsArr){
        ContentValues values;
        open();
        try{
            for(TagsRead tag: tagsReadsArr){
                values = new ContentValues();
                values.put("fecha_ultima_lectura", tag.getFechaUltimaLectura());
                values.put("estado", tag.getEstado());
                db.update("inventario", values, "tag = ?", new String[] {tag.getEpc()});
                values.clear();
                values.put("tag", tag.getEpc());
                values.put("fecha", tag.getFechaUltimaLectura());
                values.put("estado", 3);
                db.insertWithOnConflict("movimientos", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return  false;
        } finally {
            close();
        }
    }

    public boolean bajaInventario(ArrayList<TagsRead> tagsReadsArr){
        ContentValues values;
        open();
        try{
            for(TagsRead tag: tagsReadsArr){
                values = new ContentValues();
                values.put("fecha_ultima_lectura", tag.getFechaUltimaLectura());
                values.put("estado", tag.getEstado());
                db.update("inventario", values, "tag = ?", new String[] {tag.getEpc()});
                values.clear();
                values.put("tag", tag.getEpc());
                values.put("fecha", tag.getFechaUltimaLectura());
                values.put("estado", 4);
                db.insertWithOnConflict("movimientos", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return  false;
        } finally {
            close();
        }
    }

    /*****************POTENCIA*********************/
    public int obtenerPotencia() {
        open();

        String consulta = "select potencia from rfid ";
        int res;
        Cursor c = db.rawQuery(consulta, null);
        c.moveToFirst();
        if (c.getCount() != 0) {
            res = c.getInt(0);
        } else {
            res = -1;
        }
        c.close();
        return res;
    }


    public void modificarPotencia(int potencia) {
        ContentValues content;
        open();
        content = new ContentValues();
        content.put("potencia", potencia);
        db.update("rfid", content, "_id=1", null);
    }
}