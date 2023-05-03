package com.example.ps_android_control_arneses.controller.rfid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.view.clases.FontAwesome;
import com.example.ps_android_control_arneses.view.main.MenuStyle_Enum;

public class NewCF extends Fragment {
    private final MenuStyle_Enum style_enum;
    private final String iconBt2;
    private final String textBt2;
    private final String iconBt3;
    private final String textBt3;
    private NewCFL listener;

    private LinearLayout button1;
    private LinearLayout button2;
    private LinearLayout button3;
    private FontAwesome but1Icon;
    private FontAwesome but2Icon;
    private FontAwesome but3Icon;
    private TextView but1Text;
    private TextView but2Text;
    private TextView but3Text;
    private boolean secondfunction;

    public void addCFL(NewCFL listener) {
        this.listener = listener;
    }

    public NewCF(MenuStyle_Enum style_enum, String iconBt2, String textBt2, String iconBt3, String textBt3) {
        this.style_enum = style_enum;
        this.iconBt2 = iconBt2;
        this.textBt2 = textBt2;
        this.iconBt3 = iconBt3;
        this.textBt3 = textBt3;
    }

    public NewCF() {
        this.style_enum = MenuStyle_Enum.button1;
        this.iconBt2 = getContext().getResources().getString(R.string.flaticon_checked);
        this.textBt2 = getContext().getResources().getString(R.string.butEnviar);
        this.iconBt3 = getContext().getResources().getString(R.string.flaticon_checked);
        this.textBt3 = getContext().getResources().getString(R.string.butEnviar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.three_buttons, container, false);
        button1 = v.findViewById(R.id.button1);
        button1.setBackground(ContextCompat.getDrawable(getContext(), style_enum.getButDrawable()));
        but1Icon = v.findViewById(R.id.button1_icon);
        but1Text = v.findViewById(R.id.button1_text);
        button2 = v.findViewById(R.id.button2);
        button2.setBackground(ContextCompat.getDrawable(getContext(), style_enum.getButDrawable()));
        but2Icon = v.findViewById(R.id.button2_icon);
        but2Text = v.findViewById(R.id.button2_text);
        button3 = v.findViewById(R.id.button3);
        button3.setBackground(ContextCompat.getDrawable(getContext(), style_enum.getButDrawable()));
        but3Icon = v.findViewById(R.id.button3_icon);
        but3Text = v.findViewById(R.id.button3_text);
        setButton2FirstFunction();
        setButton3FirstFunction();

        button1.setOnClickListener(view -> {
            if(listener!=null) {
                listener.lecturarToggleClicked();
            }
        });

        button2.setOnClickListener(view -> {
            if(listener!=null) {
                if(!secondfunction) {
                    listener.button2Clicked();
                } else {
                    listener.button2ClickedSecond();
                }
            }
        });

        button3.setOnClickListener(view -> {
            if(listener!=null) {
                if(!secondfunction) {
                    listener.button3Clicked();
                } else {
                    listener.button3ClickedSecond();
                }
            }
        });

        return v;
    }

    public void setButton1PRessed(boolean pressed) {
        if(pressed) {
            button2.setEnabled(false);
            but1Text.setText(getResources().getString(R.string.butFin));
            but1Icon.setText(getResources().getString(R.string.rss));
            button1.setBackground(ContextCompat.getDrawable(getContext(), style_enum.getButDrawableAlt()));
        } else {
            button2.setEnabled(true);
            but1Text.setText(getResources().getString(R.string.butInit));
            but1Icon.setText(getResources().getString(R.string.wifi));
            button1.setBackground(ContextCompat.getDrawable(getContext(), style_enum.getButDrawable()));
        }
    }

    public void disableEnableRfid(boolean enable) {
        button1.setEnabled(enable);
    }

    public void setButton2FirstFunction() {
        but2Icon.setText(iconBt2);
        but2Text.setText(textBt2);
        secondfunction=false;
    }
    public void setButton3FirstFunction() {
        but3Icon.setText(iconBt3);
        but3Text.setText(textBt3);
        secondfunction=false;
    }

    public void setButton2SecondFunction() {
        but2Icon.setText(getContext().getResources().getString(R.string.broom));
        but2Text.setText(getContext().getResources().getString(R.string.butClean));
        secondfunction=true;
    }
    public void setButton3SecondFunction() {
        but2Icon.setText(getContext().getResources().getString(R.string.broom));
        but2Text.setText(getContext().getResources().getString(R.string.butClean));
        secondfunction=true;
    }
}
