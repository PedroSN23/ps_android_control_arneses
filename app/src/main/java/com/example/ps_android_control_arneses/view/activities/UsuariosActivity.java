package com.example.ps_android_control_arneses.view.activities;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.model.clases.ControlButtons;
import com.example.ps_android_control_arneses.view.fragments.ControlsFragment;
import com.example.ps_android_control_arneses.view.fragments.HeaderFragment;
import com.example.ps_android_control_arneses.view.fragments.UsuariosFragment;
import com.example.ps_android_control_arneses.view.herencia.GenericActivity;

import java.util.ArrayList;

public class UsuariosActivity extends GenericActivity {
    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    private HeaderFragment headerFragment;
    private UsuariosFragment usuariosFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_three);

        getProgresBar();

        headerFragment = new HeaderFragment(tituloActivity, style_enum.getColor(), style_enum.getColorTxt());
        headerFragment.addHeaderFragmentListner(this::onBackPressed);

        ArrayList<ControlButtons> controlButtons = new ArrayList<>();
        controlButtons.add(new ControlButtons(1, getResources().getString(R.string.butGuardar),
                getResources().getString(R.string.save),
                false,
                null));

        ControlsFragment controlsFragment = new ControlsFragment(controlButtons, style_enum);
        controlsFragment.addControlsFragmentAdapter((view, pressed) -> {
            if(!isProgresoVisible()) {
                onGuardarUsuarios();
            }
        });

        usuariosFragment = new UsuariosFragment(interfazBD);

        if(transaction.isEmpty()) {
            transaction.add(R.id.headerFragment, headerFragment);
            transaction.add(R.id.controlsFragment, controlsFragment);
            transaction.add(R.id.contentFragment, usuariosFragment).commit();
        } else {
            transaction = null;
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.headerFragment, headerFragment);
            transaction.replace(R.id.controlsFragment, controlsFragment);
            transaction.replace(R.id.contentFragment, usuariosFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        headerFragment.elevateMainBlock(54);
    }

    private void onGuardarUsuarios() {
        if(usuariosFragment.onGuardarUsuario()) {
            super.onBackPressed();
        }
    }
}