package com.example.ps_android_control_arneses.model.clases;

import android.os.Parcel;
import android.os.Parcelable;

public class ControlButtons implements Parcelable {
    private final int index;
    private final String text;
    private final String icon;
    private final boolean toggle;
    private final String text_alt;
    private boolean pressed;

    public ControlButtons(int index, String text, String icon, boolean toggle, String text_alt) {
        this.index = index;
        this.text = text;
        this.icon = icon;
        this.toggle = toggle;
        this.text_alt = text_alt;
        pressed = false;
    }

    protected ControlButtons(Parcel in) {
        this.index = in.readInt();
        this.text = in.readString();
        this.icon = in.readString();
        this.toggle = (in.readInt()==1);
        this.text_alt = in.readString();
        this.pressed = (in.readInt()==1);
    }

    public static final Creator<ControlButtons> CREATOR = new Creator<ControlButtons>() {
        @Override
        public ControlButtons createFromParcel(Parcel in) {
            return new ControlButtons(in);
        }

        @Override
        public ControlButtons[] newArray(int size) {
            return new ControlButtons[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(index);
        parcel.writeString(text);
        parcel.writeString(icon);
        parcel.writeInt(toggle? 1:0);
        parcel.writeString(text_alt);
        parcel.writeInt(pressed? 1:0);
    }

    public int getIndex() {
        return index;
    }

    public String getText() {
        return text;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isToggle() {
        return toggle;
    }

    public String getText_alt() {
        return text_alt;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isPressed() {
        return pressed;
    }
}