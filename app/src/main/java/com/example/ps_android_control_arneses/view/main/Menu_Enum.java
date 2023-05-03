package com.example.ps_android_control_arneses.view.main;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.model.clases.Permisos;

public enum Menu_Enum {
    inicio(-1, "START", R.string.info_circle, "Start", 0, 0, false, MenuStyle_Enum.start, Permisos.todos, R.color.menu1to, true),
    inventario(0, "Inventario", R.string.list, "InventarioActivity", 0, 0, false, MenuStyle_Enum.button1, Permisos.todos, R.color.menu1to, true),
    Alta(1, "Alta De Arnés", R.string.angle_double_up, "AltaArnesesActivity", 0, 1, false, MenuStyle_Enum.button2, Permisos.entradas, R.color.menu2to, true),
    Baja(1, "Baja De Arnés", R.string.angle_double_down, "BajaArnesesActivity", 0, 1, false, MenuStyle_Enum.button2, Permisos.entradas, R.color.menu2to, true),
    Prestamo(2, "Préstamo De Arnés", R.string.hand_holding, "PrestamosActivity", 0, 0, false, MenuStyle_Enum.button3, Permisos.administrador, R.color.menu3to, true),
    Devolucion(2, "Devolución De Arnés", R.string.handshake, "DevolucionActivity", 0, 0, false, MenuStyle_Enum.button3, Permisos.administrador, R.color.menu3to, true),
    HerramientasRFID(3, "Configuración RFID", R.string.cog, "Menu3ActivityRfid", 0, 0, false, MenuStyle_Enum.button4, Permisos.administrador, R.color.menu4to, true),
    Usuarios(3,"Configuración De Usuarios", R.string.user_cog, "UsuariosActivity", 0, 0, true, MenuStyle_Enum.button4, Permisos.administrador, R.color.menu4to, true),
    acerca(3, "Acerca", R.string.info, "AboutActivity", 0, 0, false, MenuStyle_Enum.button4, Permisos.todos, R.color.menu4to, true);

    private final String className;
    private final String texto;
    private final int icono;
    private final int id;
    private final int dir;
    private final int index;
    private final boolean verify;
    private final MenuStyle_Enum style_enum;
    private final Permisos permisos;
    private final int colortxt;
    private final boolean isActivity;
    Menu_Enum(int index, String texto, int icono, String className, int id, int dir, boolean verify, MenuStyle_Enum style_enum, Permisos permisos, int color, boolean isActivity) {
        this.index = index;
        this.icono = icono;
        this.texto = texto;
        this.className = className;
        this.id = id;
        this.dir = dir;
        this.verify = verify;
        this.style_enum = style_enum;
        this.permisos = permisos;
        this.colortxt = color;
        this.isActivity = isActivity;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public int getColortxt() {
        return colortxt;
    }

    public int checkPermisos(int rol) {
        return this.permisos.checkPermisos(rol);
    }

    public MenuStyle_Enum getStyle_enum() {
        return style_enum;
    }

    public boolean isVerify() {
        return verify;
    }

    public int getIndex() {
        return index;
    }

    public int getDir() {
        return dir;
    }

    public int getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public int getIcono(){
        return icono;
    }

    public String getTexto() {
        return this.texto;
    }
}
