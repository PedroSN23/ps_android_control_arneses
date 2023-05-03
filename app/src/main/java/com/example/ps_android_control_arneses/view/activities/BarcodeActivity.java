package com.example.ps_android_control_arneses.view.activities;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.model.clases.ControlButtons;
import com.example.ps_android_control_arneses.view.clases.ButtonsViewHolder;
import com.example.ps_android_control_arneses.view.fragments.BarcodeLecturasFragment;
import com.example.ps_android_control_arneses.view.fragments.ControlsFragment;
import com.example.ps_android_control_arneses.view.fragments.HeaderFragment;
import com.example.ps_android_control_arneses.view.herencia.BarcodeControllActivity;

import java.util.ArrayList;

public class BarcodeActivity extends BarcodeControllActivity {
    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    private HeaderFragment headerFragment;
    private ControlsFragment controlsFragment;
    private BarcodeLecturasFragment barcodeLecturasFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_three);

        headerFragment = new HeaderFragment(tituloActivity, style_enum.getColor(), style_enum.getColorTxt());
        headerFragment.addHeaderFragmentListner(this::onBackPressed);

        ArrayList<ControlButtons> controlButtons = new ArrayList<>();
        controlButtons.add(new ControlButtons(1, getResources().getString(R.string.butInit),
                getResources().getString(R.string.barcode),
                true,
                getResources().getString(R.string.butFinalizar)));
        controlButtons.add(new ControlButtons(2, getResources().getString(R.string.butClean),
                getResources().getString(R.string.broom),
                true,
                null));

        controlsFragment = new ControlsFragment(controlButtons, style_enum);
        controlsFragment.addControlsFragmentAdapter((view, pressed) -> {
            ButtonsViewHolder buttonsViewHolder = (ButtonsViewHolder) view.getTag();
            switch (buttonsViewHolder.cb.getIndex()) {
                case 1: //iniciar finalizar bla
                    if(pressed) {
                        startLectura();
                    } else {
                        stopLectura();
                    }
                    break;
                case 2: //limpiar
                    barcodeLecturasFragment.clearList();
                    break;
            }
        });

        barcodeLecturasFragment = new BarcodeLecturasFragment();

        if(transaction.isEmpty()) {
            transaction.add(R.id.headerFragment, headerFragment);
            transaction.add(R.id.controlsFragment, controlsFragment);
            transaction.add(R.id.contentFragment, barcodeLecturasFragment).commit();
        } else {
            transaction = null;
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.headerFragment, headerFragment);
            transaction.replace(R.id.controlsFragment, controlsFragment);
            transaction.replace(R.id.contentFragment, barcodeLecturasFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        headerFragment.elevateMainBlock(54);
    }

    @Override
    public void onBarcodeStarted(boolean b) {
        controlsFragment.setButtonPressed(1, b);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBarcodeRead(String barCode) {
        if(barcodeLecturasFragment.addBarcode(barCode)) {
            soundTagRead();
        }
    }
}