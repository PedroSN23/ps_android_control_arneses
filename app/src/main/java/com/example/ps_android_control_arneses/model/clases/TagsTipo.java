package com.example.ps_android_control_arneses.model.clases;

public enum TagsTipo {
    tagactivo(1),
    ninguno(0);

    private final int code;

    TagsTipo(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
