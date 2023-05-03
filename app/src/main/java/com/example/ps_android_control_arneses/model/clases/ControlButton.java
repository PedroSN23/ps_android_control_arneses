package com.example.ps_android_control_arneses.model.clases;

import android.os.Parcel;
import android.os.Parcelable;

public class ControlButton implements Parcelable {
    private final int index;
    private final String text;
    private final String icon;
    private final boolean toggle;
    private final String text_alt;
    private boolean pressed;
    private final boolean slash;
    private boolean wait;

    public ControlButton(int index, String text, String icon, boolean toggle, String text_alt, boolean slash) {
        this.index = index;
        this.text = text;
        this.icon = icon;
        this.toggle = toggle;
        this.text_alt = text_alt;
        this.slash = slash;
        pressed = false;
        wait = false;
    }

    protected ControlButton(Parcel in) {
        this.index = in.readInt();
        this.text = in.readString();
        this.icon = in.readString();
        this.toggle = (in.readInt()==1);
        this.text_alt = in.readString();
        this.pressed = (in.readInt()==1);
        this.slash = (in.readInt()==1);
    }

    public static final Creator<ControlButton> CREATOR = new Creator<ControlButton>() {
        @Override
        public ControlButton createFromParcel(Parcel in) {
            return new ControlButton(in);
        }

        @Override
        public ControlButton[] newArray(int size) {
            return new ControlButton[size];
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
        parcel.writeInt(slash? 1:0);
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

    public boolean isSlash() {
        return slash;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public boolean isWait() {
        return this.wait;
    }
}
