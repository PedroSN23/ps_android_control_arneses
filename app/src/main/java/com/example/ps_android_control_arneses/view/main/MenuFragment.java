package com.example.ps_android_control_arneses.view.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.view.clases.FontAwesome;

public class MenuFragment extends Fragment {
    int layoutid;
    boolean emptyFragment;
    private final LinearLayout[] linearLayouts = new LinearLayout[5];

    public MenuFragment(int layoutid) {
        emptyFragment=false;
        this.layoutid = layoutid;
    }

    public MenuFragment() {
        emptyFragment=true;
        this.layoutid = R.layout.menu1_fragment;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(layoutid, container, false);
        if(!emptyFragment) {
            LinearLayout parent = v.findViewById(R.id.parentContainer);

            int i=0;
            int index = Integer.parseInt((String) v.getTag());
            for (Menu_Enum m : Menu_Enum.values()) {
                if (m.getIndex() == index) {
                    @SuppressLint("InflateParams") View h = inflater.inflate(R.layout.submenu_button, null, false);
                    linearLayouts[i] = h.findViewById(R.id.submenu);
                    linearLayouts[i].setTag(m);
                    linearLayouts[i].setBackground(ContextCompat.getDrawable(getContext(), m.getStyle_enum().getButDrawable()));
                    FontAwesome icono = h.findViewById(R.id.icono);
                    icono.setText(getResources().getString(m.getIcono()));
                    TextView texto = h.findViewById(R.id.texto);
                    texto.setText(m.getTexto());
                    parent.addView(h);
                    i++;
                }
            }
        }
        return v;
    }

    public void buttonClicked(int buttomClicked) {
        if(buttomClicked<linearLayouts.length && linearLayouts[buttomClicked]!=null) {
            linearLayouts[buttomClicked].performClick();
        }
    }
}
