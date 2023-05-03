package com.example.ps_android_control_arneses.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.model.clases.ControlButtons;
import com.example.ps_android_control_arneses.view.clases.ButtonsViewHolder;
import com.example.ps_android_control_arneses.view.main.MenuStyle_Enum;

import java.util.ArrayList;

public class ControlsFragment extends Fragment {
    private final MenuStyle_Enum style_enum;
    private ArrayList<ButtonsViewHolder> buttons;

    private ControlsFragmentListener listener;

    public ControlsFragment(ArrayList<ControlButtons> controlButtons, MenuStyle_Enum style_enum) {
        this.style_enum = style_enum;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("controlButtons", controlButtons);
        setArguments(bundle);
    }


    public void addControlsFragmentAdapter(ControlsFragmentListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);

        Bundle bundle = getArguments();
        if(bundle!=null) {
            ArrayList<ControlButtons> controlButtons = bundle.getParcelableArrayList("controlButtons");
            buttons = new ArrayList<>();
            for(ControlButtons cb: controlButtons) {
                View view = crearBoton(cb, inflater, layout);
                layout.addView(view);
            }
        }
        return layout;
    }

    @SuppressWarnings("ConstantConditions")
    private View crearBoton(ControlButtons cb, LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.generic_button, container, false);
        ButtonsViewHolder viewHolder = new ButtonsViewHolder();
        viewHolder.cb = cb;
        viewHolder.buton = v.findViewById(R.id.buttonMain);
        viewHolder.buton.setBackground(ContextCompat.getDrawable(getContext(), style_enum.getButDrawable()));
        viewHolder.icono = v.findViewById(R.id.icono);
        viewHolder.icono.setText(cb.getIcon());
        viewHolder.slash = v.findViewById(R.id.slash);
        viewHolder.texto = v.findViewById(R.id.texto);
        viewHolder.texto.setText(cb.getText());
        viewHolder.buton.setTag(viewHolder);
        viewHolder.buton.setOnClickListener(view -> {
            if(listener!=null) {
                ButtonsViewHolder buttonsViewHolder = (ButtonsViewHolder) view.getTag();
                listener.buttonClicked(view, !buttonsViewHolder.cb.isPressed());
            }
        });
        buttons.add(viewHolder);
        return v;
    }

    @SuppressWarnings("ConstantConditions")
    public void setButtonPressed(View view, boolean pressed) {
        ButtonsViewHolder viewHolder = (ButtonsViewHolder)view.getTag();
        if(viewHolder!=null) {
            if(viewHolder.cb.isToggle()) {
                if(pressed) {
                    viewHolder.texto.setText(viewHolder.cb.getText_alt());
                    viewHolder.slash.setVisibility(View.VISIBLE);
                    viewHolder.buton.setBackground(ContextCompat.getDrawable(getContext(), style_enum.getButDrawableAlt()));
                } else  {
                    viewHolder.texto.setText(viewHolder.cb.getText());
                    viewHolder.slash.setVisibility(View.GONE);
                    viewHolder.buton.setBackground(ContextCompat.getDrawable(getContext(), style_enum.getButDrawable()));
                }
            }
        }
    }

    public void setButtonPressed(int i, boolean pressed) {
        for(ButtonsViewHolder bvh: buttons) {
            if(bvh.cb.getIndex()==i) {
                bvh.cb.setPressed(pressed);
                setButtonPressed(bvh.buton, pressed);
                break;
            }
        }
    }
}
