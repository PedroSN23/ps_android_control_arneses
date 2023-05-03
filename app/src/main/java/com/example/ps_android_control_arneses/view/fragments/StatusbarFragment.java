package com.example.ps_android_control_arneses.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.view.clases.FontAwesome;

public class StatusbarFragment extends Fragment {
    private final int drawable;

    private TextView mensajeTv=null;
    private FontAwesome statusFa=null;
    private Context context;

    public StatusbarFragment(int drawable) {
        this.drawable = drawable;
    }

    public StatusbarFragment() {
        this.drawable = R.drawable.menu1_button_up;
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.statusbar, container, false);
        mensajeTv = v.findViewById(R.id.textoMsg);
        mensajeTv.setBackground(ContextCompat.getDrawable(getContext(), drawable));
        statusFa = v.findViewById(R.id.iconoStatus);
        statusFa.setBackground(ContextCompat.getDrawable(getContext(), drawable));
        context = getContext();
        return v;
    }

    @Override
    public void onDestroyView() {
        mensajeTv=null;
        statusFa=null;
        super.onDestroyView();
    }

    public void setTextMessage(String msg) {
        if(mensajeTv!=null) {
            mensajeTv.setText(msg);
        }
    }

    public void setStatusIcon(int code) {
        if(statusFa!=null) {
            switch (code) {
                case 0:
                    statusFa.setText(context.getResources().getString(R.string.exclamation_circle));
                    statusFa.setTextColor(ContextCompat.getColor(context, R.color.faltante));
                    break;
                case 1:
                    statusFa.setText(context.getResources().getString(R.string.check_circle));
                    statusFa.setTextColor(ContextCompat.getColor(context, R.color.bien));
                    break;
                case -1:
                    statusFa.setText(context.getResources().getString(R.string.times_circle));
                    statusFa.setTextColor(ContextCompat.getColor(context, R.color.sobrante));
                    break;
                case 2:
                    statusFa.setText(context.getResources().getString(R.string.wifi));
                    statusFa.setTextColor(ContextCompat.getColor(context, R.color.azulAstlix));
                    break;
                case 3:
                    statusFa.setText(context.getResources().getString(R.string.edit));
                    statusFa.setTextColor(ContextCompat.getColor(context, R.color.azulAstlix));
                    break;
            }
        }
    }
}
