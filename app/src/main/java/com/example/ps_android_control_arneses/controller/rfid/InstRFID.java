package com.example.ps_android_control_arneses.controller.rfid;

public enum InstRFID {
    instance(0),
    iniciar(1),
    changePower(2),
    inventoryFilter(3),
    ciega(4),
    finalizar(5),
    none(6),
    tags(7),
    ready(8),
    grabar(9),
    busquedaFilter(10),
    rssi(11);

    private final int code;

    InstRFID(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
