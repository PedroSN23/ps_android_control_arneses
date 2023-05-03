package com.example.ps_android_control_arneses.controller.rfid;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.rscja.deviceapi.RFIDWithUHFUART;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class RFIDActivityHandler extends Handler {
    private static final String MSG_RFID_EPCS = "epcs";
    private static final String MSG_RFID_RSSI = "rssi";
    WeakReference<Handler> mainHandler;
    WeakReference<RFIDWithUHFUART> mReader;
    WeakReference<AtomicBoolean> read;

    private RFIDActivityHandlerListener listener;

    private final ToneGenerator tonG;

    public RFIDActivityHandler() {
        tonG = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
        this.mainHandler = new WeakReference(mainHandler);
        this.mReader = new WeakReference(mReader);
        this.read = new WeakReference(read);
    }

    public void addRFIDActivityHandlerAdapter(RFIDActivityHandlerListener listener) {
        this.listener = listener;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (listener != null) {
            InstRFID instRFID = InstRFID.none;
            for (InstRFID r : InstRFID.values()) {
                if (r.getCode() == msg.arg1) {
                    instRFID = r;
                    break;
                }
            }
            switch (instRFID) {
                case instance:
                    if (msg.arg2 == RespRFID.success.getCode()) {
                        listener.instanceSuccess();
                    } else {
                        listener.setError((String) msg.obj);
                    }
                    break;
                case iniciar:
                    if (msg.arg2 == RespRFID.success.getCode()) {
                        listener.iniciarSuccess();
                    } else {
                        listener.setError((String) msg.obj);
                    }
                    break;
                case changePower:
                    if (msg.arg2 == RespRFID.success.getCode()) {
                        listener.changePowerSuccess();
                    } else {
                        listener.setError((String) msg.obj);
                    }
                    break;
                case inventoryFilter:
                case ciega:
                    if (msg.arg2 == RespRFID.error.getCode()) {
                        listener.setError((String) msg.obj);
                    } else {
                        if (msg.arg2 == RespRFID.neutro.getCode()) {
                            listener.lecturaFinalizada();
                        } else {
                            listener.lecturaIniciada();
                        }
                    }
                    break;
                case rssi:
                    Bundle bundle1 = msg.getData();
                    listener.enviarRssi(bundle1.getDouble(MSG_RFID_RSSI));
                    break;
                case none:
                    break;
                case ready:
                    listener.threadReady();
                    break;
                case finalizar:
                    listener.finalizar();
                    break;
                case tags:
                    Bundle bundle = msg.getData();
                    if (msg.arg2 == InstRFID.inventoryFilter.getCode()) {
                        listener.tagsLecturaInventario(bundle.getStringArrayList(MSG_RFID_EPCS));
                    } else { //ciega
                        listener.tagsLecturaCiega(bundle.getStringArrayList(MSG_RFID_EPCS), bundle.getStringArrayList(MSG_RFID_RSSI));
                    }
                    break;
                case grabar:
                    if (msg.arg2 == RespRFID.success.getCode()) {
                        listener.grabarSuccess();
                    } else {
                        listener.setError((String) msg.obj);
                    }
                    break;
            }
        }
    }
}

