package com.example.ps_android_control_arneses.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.view.clases.FontAwesome;

public class HeaderFragment extends Fragment {
    private final String titulo;
    private final int color;
    private final int colorTxt;
    private FontAwesome backIcon;

    private RelativeLayout mainBlock;

    private HeaderFragmentAdapter listner;

    public HeaderFragment(String titulo, int color, int colortxt) {
        this.titulo = titulo;
        this.color = color;
        this.colorTxt = colortxt;
    }

    public void addHeaderFragmentListner(HeaderFragmentAdapter listener) {
        this.listner = listener;
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContext(), color));
        View v = inflater.inflate(R.layout.header, container, false);
        mainBlock = v.findViewById(R.id.menuEncabezado);
        mainBlock.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        TextView tituloTv = v.findViewById(R.id.titulo);
        tituloTv.setText(titulo);
        tituloTv.setTextColor(ContextCompat.getColor(getContext(), colorTxt));
        backIcon = v.findViewById(R.id.backButton);
        backIcon.setTextColor(ContextCompat.getColor(getContext(), colorTxt));
        backIcon.setOnClickListener(view -> {
            if(listner!=null) {
                listner.backPressed();
            }
        });
        return v;
    }

    public void elevateMainBlock(float val) {
        //mainBlock.setElevation(val);
    }

    public void backIconClicked() {
        backIcon.performClick();
    }
}