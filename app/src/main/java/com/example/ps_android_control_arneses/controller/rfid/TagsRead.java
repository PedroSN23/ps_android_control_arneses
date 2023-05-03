package com.example.ps_android_control_arneses.controller.rfid;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TagsRead implements Parcelable {
    private final String epc;
    private int count;
    private double rssi;
    private String tag;
    private String fecha_alta;
    private String fecha_ultima_lectura;
    private int estado;
    private String texto;

    public TagsRead(String epc, double rssi) {
        this.epc = epc;
        this.rssi = rssi;
        this.count = count;
    }

    public TagsRead(String epc, String fecha, String texto, Integer estado) {
        this.epc = epc;
        this.fecha_alta = fecha;
        this.fecha_ultima_lectura = fecha;
        this.texto = texto;
        this.estado = estado;
    }
    public TagsRead(String epc) {
        this.epc = epc;

    }

    protected TagsRead(Parcel in) {
        epc = in.readString();
        count = in.readInt();
        rssi = in.readDouble();
    }

    public static final Creator<TagsRead> CREATOR = new Creator<TagsRead>() {
        @Override
        public TagsRead createFromParcel(Parcel in) {
            return new TagsRead(in);
        }

        @Override
        public TagsRead[] newArray(int size) {
            return new TagsRead[size];
        }
    };
    public String getTag() {
        return tag;
    }

    public String getFechaAlta() {
        return fecha_alta;
    }

    public String getFechaUltimaLectura() {
        return fecha_ultima_lectura;
    }

    public String getEpc() {
        return epc;
    }
    public Integer getEstado(){
        return estado;
    }
    public String getTexto(){
        return texto;
    }
    public double getRssi() {
        return rssi;
    }

    public int getcount() {
        return count;
    }

    public void addCount() {
        count++;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public int compareTo(int count) {
        return count-this.count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(epc);
        dest.writeInt(count);
        dest.writeDouble(rssi);
    }
}
