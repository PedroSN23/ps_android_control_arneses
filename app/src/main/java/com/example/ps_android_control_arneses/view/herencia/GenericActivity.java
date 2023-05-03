package com.example.ps_android_control_arneses.view.herencia;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.model.database.InterfazBD;
import com.example.ps_android_control_arneses.view.main.MenuStyle_Enum;

import java.util.Locale;

public class GenericActivity extends AppCompatActivity {
    public MenuStyle_Enum style_enum;
    public InterfazBD interfazBD;
    public String tituloActivity;
    private RelativeLayout progreso;
    private TextView textProgres;
    private boolean progresoVisible = false;
    private boolean progresWithText = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int styleCode = bundle.getInt("styleCode", 1);
            style_enum = MenuStyle_Enum.button1;
            for (MenuStyle_Enum m : MenuStyle_Enum.values()) {
                if (m.getCode() == styleCode) {
                    style_enum = m;
                    break;
                }
                tituloActivity = bundle.getString("texto");
            }

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, style_enum.getColor()));
        }

        interfazBD = new InterfazBD(this);
    }

    @Override
    protected void onDestroy() {
        interfazBD.close();
        super.onDestroy();
    }

    public void getProgresBar() {
        progreso = findViewById(R.id.progress);
        ProgressBar progressBar = findViewById(R.id.circle);
        //progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, style_enum.getColor()), PorterDuff.Mode.SRC_IN);
    }

    public void getProgresBarWithText() {
        progresWithText = true;
        progreso = findViewById(R.id.progress);
        ProgressBar progressBar = findViewById(R.id.circle);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, style_enum.getColor()), PorterDuff.Mode.SRC_IN);
        textProgres = findViewById(R.id.progresoTv);
    }

    public boolean isProgresoVisible() {
        return progresoVisible;
    }

    public void setProgresoVisible(boolean b) {
        progresoVisible = b;
        progreso.setVisibility((b) ? View.VISIBLE : View.GONE);
        if (progresWithText) {
            textProgres.setVisibility((b) ? View.VISIBLE : View.GONE);
        }
    }

    public void setProgressVal(int value) {
        if (progresWithText) {
            textProgres.setText(String.format(Locale.getDefault(), "%d%c", value, '%'));
        }
    }
}