package com.example.ps_android_control_arneses.model.clases;

public enum Permisos {
    todos(1, 1, 1, 1),
    administrador(1, 1, 0, 0),
    entradas(1, 1, 0, 1),
    salidas(1, 1, 1, 0);

    private int[] permisos=new int[4];

    Permisos(int root, int adm, int salidas, int entradas) {
        permisos[0]=root;
        permisos[1]=adm;
        permisos[2]=salidas;
        permisos[3]=entradas;
    }

    public int checkPermisos(int rol) {
        if(rol>=0 && rol<permisos.length) {
            return permisos[rol];
        }
        return 0;
    }
}
