package com.example.ps_android_control_arneses.controller.rfid;

public enum RespRFID {
    error(-1),
    neutro(0),
    success(1);

    private final int code;

    RespRFID(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
