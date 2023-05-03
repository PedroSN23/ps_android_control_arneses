package com.example.ps_android_control_arneses.model.clases;

public class Usuarios {
    private final String usuario;
    private String contrasena;
    private String newContrasena;
    private boolean nueva;
    private int rol;
    private int id;

    public Usuarios(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = -1;
        this.id = -1;
        this.nueva = false;
    }

    public boolean isNueva() {
        return this.nueva;
    }

    public String getNewContrasena() {
        this.nueva=false;
        return this.newContrasena;
    }

    public void setNewContrasena(String contrasena) {
        this.newContrasena = contrasena;
        this.nueva=true;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return usuario+" "+contrasena+" "+rol+" "+id+" "+newContrasena;
    }
}
