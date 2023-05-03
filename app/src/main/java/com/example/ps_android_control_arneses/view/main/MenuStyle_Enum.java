package com.example.ps_android_control_arneses.view.main;

import com.example.ps_android_control_arneses.R;

public enum MenuStyle_Enum {
    start(0, R.drawable.button, R.drawable.button_alt, R.drawable.button_up, R.drawable.button_down, R.color.loginp, R.color.menu1to),
    button1(1, R.drawable.menu1_button, R.drawable.menu1_button_alt, R.drawable.menu1_button_up, R.drawable.menu1_button_down, R.color.menu1p, R.color.menu1to),
    button2(2, R.drawable.menu2_button, R.drawable.menu2_button_alt, R.drawable.menu2_button_up, R.drawable.menu2_button_down, R.color.menu2p, R.color.menu2to),
    button3(3, R.drawable.menu3_button, R.drawable.menu3_button_alt, R.drawable.menu3_button_up, R.drawable.menu3_button_down, R.color.menu3p, R.color.menu3to),
    button4(4, R.drawable.menu4_button, R.drawable.menu4_button_alt, R.drawable.menu4_button_up, R.drawable.menu4_button_down, R.color.menu4p, R.color.menu3to);

    private final int code;
    private final int butDrawable;
    private final int butDrawableAlt;
    private final int butUp;
    private final int butDown;
    private final int color;
    private final int colorTxt;

    MenuStyle_Enum(int code, int butDrawable, int butDrawableAlt, int butUp, int butDown, int color, int colorTxt) {
        this.code = code;
        this.butDrawable = butDrawable;
        this.butDrawableAlt = butDrawableAlt;
        this.butUp = butUp;
        this.butDown = butDown;
        this.color = color;
        this.colorTxt = colorTxt;
    }

    public int getColorTxt() {
        return colorTxt;
    }

    public int getCode() {
        return code;
    }

    public int getButDrawable() {
        return butDrawable;
    }

    public int getButDrawableAlt() {
        return butDrawableAlt;
    }

    public int getButUp() {
        return butUp;
    }

    public int getButDown() {
        return butDown;
    }

    public int getColor() {
        return color;
    }
}