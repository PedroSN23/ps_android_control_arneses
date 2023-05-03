package com.example.ps_android_control_arneses.controller.rfid;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.ps_android_control_arneses.R;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RfidThread extends Thread {

    private static final String MSG_RFID_POT = "pot";
    private static final String MSG_RFID_EPC = "epc";
    private static final String MSG_RFID_EPCS = "epcs";
    private static final String MSG_RFID_RSSI = "rssi";

    public ThreadHandlerRfid myThreadHandler;
    private final Handler mainHandler;
    private RFIDWithUHFUART mReader;
    private final AtomicBoolean read = new AtomicBoolean(true);

    private final Activity myActivity;

    public RfidThread(Handler mainHandler, Activity myActivity) {
        this.mainHandler = mainHandler;
        this.myActivity = myActivity;
        Message msgOut = mainHandler.obtainMessage();
        msgOut.arg1 = InstRFID.instance.getCode();
        try {
            mReader = RFIDWithUHFUART.getInstance(); //RFIDWithUHF.getInstance();
            msgOut.arg2 = RespRFID.success.getCode();
        } catch (Exception ex) {
            msgOut.obj = ex.getMessage();
            msgOut.arg2 = RespRFID.error.getCode();
        }
        mainHandler.sendMessage(msgOut);
    }

   public static class ThreadHandlerRfid extends Handler {
        WeakReference<RFIDWithUHFUART> mReader;
        WeakReference<Handler> mainHandler;
        private boolean intRfid;
        WeakReference<AtomicBoolean> read;
        WeakReference<Activity> mainActivity;

        @SuppressWarnings("deprecation")
        public ThreadHandlerRfid(RFIDWithUHFUART mReader, Handler mainHandler, AtomicBoolean read, Activity mainActivity) {
            this.mReader = new WeakReference<>(mReader);
            this.mainHandler = new WeakReference<>(mainHandler);
            this.read = new WeakReference<>(read);
            this.mainActivity = new WeakReference<>(mainActivity);
        }

        @SuppressWarnings("BusyWait")
        public void handleMessage(Message msgIn) {
            Bundle bundle;
            Message msgOut = mainHandler.get().obtainMessage();
            msgOut.arg1 = msgIn.arg1;
            InstRFID instRFID = InstRFID.none;
            for(InstRFID r: InstRFID.values()) {
                if(r.getCode()==msgIn.arg1) {
                    instRFID = r;
                    break;
                }
            }
            switch (instRFID) {
                case instance:
                    msgOut.arg2 = RespRFID.neutro.getCode();
                    break;
                case iniciar: //iniciar thread
                    intRfid = mReader.get().init();
                    if (intRfid) {
                        if (mReader.get().setFrequencyMode((byte) 8)) {
                            msgOut.arg2 = RespRFID.success.getCode();
                        } else {
                            msgOut.arg2 = RespRFID.error.getCode();
                            msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidFreq);
                        }
                    } else {
                        msgOut.arg2 = RespRFID.error.getCode();
                        msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidInit);
                    }
                    break;
                case changePower: //change power
                    if (intRfid) {
                        bundle = msgIn.getData();
                        int potencia = bundle.getInt(MSG_RFID_POT);
                        mReader.get().setPower(potencia);
                        msgOut.arg2 = RespRFID.success.getCode();
                    } else {
                        msgOut.arg2 = RespRFID.error.getCode();
                        msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidNotInit);
                    }
                    break;
                case inventoryFilter: //start lectura zapatos
                    if (intRfid) {
                        read.get().set(true);
                        mReader.get().setFilter(RFIDWithUHFUART.Bank_EPC, 32, 20, "495441");
                        if (mReader.get().startInventoryTag()) { //(byte) 0, (byte) 0)) {
                            msgOut.arg2 = RespRFID.success.getCode();
                            mainHandler.get().sendMessage(msgOut);
                            while (read.get().get()) {
                                ArrayList<String> epcs = new ArrayList<>();
                                boolean found = false;
                                int cantidad = 0;
                                UHFTAGInfo uhftagInfo;
                                while ((uhftagInfo = mReader.get().readTagFromBuffer()) != null) {
                                    String tmpEpc = uhftagInfo.getEPC();
                                    //if(tmpEpc.length()>=40) {
                                        cantidad++;
                                        found = true;
                                        epcs.add(tmpEpc);
                                    //}
                                    if (cantidad >= 100) break;
                                }
                                if (found) {
                                    Message tmpMsg = mainHandler.get().obtainMessage();
                                    tmpMsg.arg1 = InstRFID.tags.getCode();
                                    tmpMsg.arg2 = msgIn.arg1;
                                    bundle = new Bundle();
                                    bundle.putStringArrayList(MSG_RFID_EPCS, epcs);
                                    tmpMsg.setData(bundle);
                                    mainHandler.get().sendMessage(tmpMsg);
                                }
                                try {
                                    sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            msgOut = mainHandler.get().obtainMessage();
                            msgOut.arg1 = msgIn.arg1;
                            msgOut.arg2 = RespRFID.neutro.getCode();
                            mReader.get().stopInventory();
                            while (mReader.get().readTagFromBuffer() != null) {
                                assert true;
                            }
                        } else {
                            msgOut.arg2 = RespRFID.error.getCode();
                            msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidInv);
                        }
                    } else {
                        msgOut.arg2 = RespRFID.error.getCode();
                        msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidNotInit);
                    }
                    break;
                case finalizar: //finalizar
                    msgOut.arg2 = RespRFID.neutro.getCode();
                    try {
                        Log.e("FREE", "antes de free");
                        mReader.get().free();
                        Log.e("FREE", "despues de free");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    intRfid=false;
                    break;
                case none: //dummy
                    msgOut.arg2 = RespRFID.neutro.getCode();
                    break;
                case ciega: //start inventory loop
                    if (intRfid) {
                        read.get().set(true);
                        mReader.get().setFilter(RFIDWithUHFUART.Bank_EPC, 32, 0, "3030");
                        if (mReader.get().startInventoryTag()) {
                            while (read.get().get()) {
                                ArrayList<String> epcs = new ArrayList<>();
                                ArrayList<String> rssi = new ArrayList<>();
                                boolean found = false;
                                int cantidad = 0;
                                UHFTAGInfo uhftagInfos;
                                while ((uhftagInfos = mReader.get().readTagFromBuffer()) != null) {
                                    String tmpEpc = uhftagInfos.getEPC();
                                    cantidad++;
                                    found = true;
                                    epcs.add(tmpEpc);
                                    rssi.add(uhftagInfos.getRssi());
                                    if (cantidad >= 100) break;
                                }
                                if (found) {
                                    Message msgTmp = mainHandler.get().obtainMessage();
                                    msgTmp.arg1 = InstRFID.tags.getCode();
                                    msgTmp.arg2 = msgIn.arg1;
                                    bundle = new Bundle();
                                    bundle.putStringArrayList(MSG_RFID_EPCS, epcs);
                                    bundle.putStringArrayList(MSG_RFID_RSSI, rssi);
                                    msgTmp.setData(bundle);
                                    mainHandler.get().sendMessage(msgTmp);
                                }
                                try {
                                    sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            msgOut.arg2 = RespRFID.neutro.getCode();
                            mReader.get().stopInventory();
                            while (mReader.get().readTagFromBuffer() != null) {
                                assert true;
                            }
                        } else {
                            msgOut.arg2 = RespRFID.error.getCode();
                            msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidInv);
                        }
                    } else {
                        msgOut.arg2 = RespRFID.error.getCode();
                        msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidNotInit);
                    }
                    break;
                case busquedaFilter:
                    if (intRfid) {
                        read.get().set(true);
                        String filtro = (String) msgIn.obj;
                        mReader.get().setFilter(RFIDWithUHFUART.Bank_EPC, 32, filtro.length()*4, filtro);
                        if (mReader.get().startInventoryTag()) { //(byte) 0, (byte) 0)) {
                            msgOut.arg2 = RespRFID.success.getCode();
                            mainHandler.get().sendMessage(msgOut);
                            while (read.get().get()) {
                                UHFTAGInfo uhftagInfo;
                                double maxRssi = -85.0;
                                while ((uhftagInfo = mReader.get().readTagFromBuffer()) != null) {
                                    try {
                                        double rssi = Double.parseDouble(uhftagInfo.getRssi());
                                        if(maxRssi<rssi) {
                                            maxRssi=rssi;
                                        }
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Message tmpMsg = mainHandler.get().obtainMessage();
                                tmpMsg.arg1 = InstRFID.rssi.getCode();
                                tmpMsg.arg2 = msgIn.arg1;
                                bundle = new Bundle();
                                bundle.putDouble(MSG_RFID_RSSI, maxRssi);
                                tmpMsg.setData(bundle);
                                mainHandler.get().sendMessage(tmpMsg);
                                try {
                                    sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            msgOut = mainHandler.get().obtainMessage();
                            msgOut.arg1 = msgIn.arg1;
                            msgOut.arg2 = RespRFID.neutro.getCode();
                            mReader.get().stopInventory();
                            while (mReader.get().readTagFromBuffer() != null) {
                                assert true;
                            }
                        } else {
                            msgOut.arg2 = RespRFID.error.getCode();
                            msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidInv);
                        }
                    } else {
                        msgOut.arg2 = RespRFID.error.getCode();
                        msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidNotInit);
                    }
                    break;

                case grabar:
                    if (intRfid) {
                        bundle = msgIn.getData();
                        String epcNuevo = bundle.getString(MSG_RFID_EPC);
                        mReader.get().setFilter(RFIDWithUHFUART.Bank_EPC, 32, 0, "3030");
                        String first = mReader.get().readData("00000000", RFIDWithUHFUART.Bank_EPC, 1, 1);
                        if(first!=null) {
                            if(mReader.get().writeData("00000000", RFIDWithUHFUART.Bank_EPC, 1, 1, "7800")) {
                                if(mReader.get().writeData("00000000", RFIDWithUHFUART.Bank_EPC, 2, 15, epcNuevo)) {
                                    msgOut.arg2 = RespRFID.success.getCode();
                                } else {
                                    msgOut.arg2 = RespRFID.error.getCode();
                                    msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidTagWrite);
                                }
                            } else {
                                msgOut.arg2 = RespRFID.error.getCode();
                                msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidTagSize);
                            }
                        } else {
                            msgOut.arg2 = RespRFID.error.getCode();
                            msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidNoTag);
                        }
                    } else {
                        msgOut.arg2 = RespRFID.error.getCode();
                        msgOut.obj = mainActivity.get().getResources().getString(R.string.errRfidNotInit);
                    }
                    break;
            }
            mainHandler.get().sendMessage(msgOut);
        }
    }

    @Override
    public void run() {
        Looper.prepare();

        Message msgInit = mainHandler.obtainMessage();
        msgInit.arg1 = InstRFID.ready.getCode();
        msgInit.arg2 = RespRFID.neutro.getCode();

        myThreadHandler = new ThreadHandlerRfid(mReader, mainHandler, read, myActivity);

        mainHandler.sendMessage(msgInit);

        Looper.loop();
    }

    public void stopReading() {
        read.set(false);
    }

    public void iniciarRfid() {
        Message msg = new Message();
        msg.arg1 = InstRFID.iniciar.getCode();
        myThreadHandler.sendMessage(msg);
    }

    public void finalizarRfid() {
        read.set(false);
        Message msg = new Message();
        msg.arg1 = InstRFID.finalizar.getCode();
        myThreadHandler.sendMessage(msg);
    }

    public void startLecturaPuertas() {
        Message msg = new Message();
        msg.arg1 = InstRFID.inventoryFilter.getCode();
        myThreadHandler.sendMessage(msg);
    }

    public void startLecturaCiega() {
        Message msg = new Message();
        msg.arg1 = InstRFID.ciega.getCode();
        myThreadHandler.sendMessage(msg);
    }

    public void startLecturaBusqueda(String filtro) {
        Message msg = new Message();
        msg.arg1 = InstRFID.busquedaFilter.getCode();
        msg.obj = filtro;
        myThreadHandler.sendMessage(msg);
    }

    public void setPotencia(int potencia) {
        Message msg = new Message();
        msg.arg1 = InstRFID.changePower.getCode();
        Bundle bundle = new Bundle();
        bundle.putInt(MSG_RFID_POT, Math.max(Math.min(potencia, 30), 5));
        msg.setData(bundle);
        myThreadHandler.sendMessage(msg);
    }

    public void grabarTag(String epcVal) {
        Message msg = new Message();
        msg.arg1 = InstRFID.grabar.getCode();
        Bundle bundle = new Bundle();
        bundle.putString(MSG_RFID_EPC, epcVal);
        msg.setData(bundle);
        myThreadHandler.sendMessage(msg);
    }
}
