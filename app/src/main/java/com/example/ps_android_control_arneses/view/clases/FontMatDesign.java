package com.example.ps_android_control_arneses.view.clases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class FontMatDesign extends TextView {
    public FontMatDesign(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FontMatDesign(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontMatDesign(Context context) {
        super(context);
        init();
    }

    private void init() {
        //Font name should not contain "/".
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/matdesigniconfont.otf");
        setTypeface(tf);
    }
}
