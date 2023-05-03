package com.example.ps_android_control_arneses.controller.rfid;

import java.util.ArrayList;

public interface RFIDActivityHandlerListener {
    void setError(String msg);
    void iniciarSuccess();
    void instanceSuccess();
    void changePowerSuccess();
    void lecturaIniciada();
    void lecturaFinalizada();
    void threadReady();
    void finalizar();
    void tagsLecturaInventario(ArrayList<String> stringArrayList);
    void tagsLecturaCiega(ArrayList<String> stringArrayList, ArrayList<String> stringArrayList1);
    void grabarSuccess();
    void enviarRssi(double aDouble);
}

