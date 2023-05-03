package com.example.ps_android_control_arneses.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.model.database.InterfazBD;
import com.google.android.material.textfield.TextInputEditText;
import com.rscja.utility.StringUtility;

import java.util.ArrayList;

public class UsuariosFragment extends Fragment {
    private final InterfazBD interfazBD;
    private final TextInputEditText[] usser = new TextInputEditText[2];
    private final TextInputEditText[] pass = new TextInputEditText[2];

    public UsuariosFragment(InterfazBD interfazBD) {
        this.interfazBD = interfazBD;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_usuarios, container, false);
        ArrayList<String> usuarios = interfazBD.obtenerUsuarios();

        usser[0] = v.findViewById(R.id.adminUsr);
        usser[0].setKeyListener(null);
        usser[1] = v.findViewById(R.id.userUsr);
        pass[0] = v.findViewById(R.id.admPass);
        pass[1] = v.findViewById(R.id.userPass);

        for(int i=0; i<usuarios.size(); i++) {
            usser[i].setText(usuarios.get(i));
        }
        return v;
    }

    @SuppressWarnings("ConstantConditions")
    public boolean onGuardarUsuario() {
        boolean error=false;
        String [] usr = new String[2];
        String [] pas = new String[2];
        int[] index = new int[2];

        for(int i=0; i<2; i++) {
            index[i]=-1;
            usr[i]=usser[i].getText().toString();
            pas[i]=pass[i].getText().toString();
            if(!StringUtility.isEmpty(usr[i]) && !StringUtility.isEmpty(pas[i])) {
                if(pas[i].length()<4) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.errPswLen)+i, Toast.LENGTH_LONG).show();
                    error=true;
                    break;
                } else {
                    if(usr[i].compareTo("root")==0) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.errUsrRes), Toast.LENGTH_LONG).show();
                        error=true;
                        break;
                    } else {
                        if (i > 0) {
                            if (usr[0].compareTo(usr[i]) == 0) {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.errUsrDup), Toast.LENGTH_LONG).show();
                                error = true;
                                break;
                            } else {
                                index[i]=1;
                            }
                        } else {
                            index[i]=1;
                        }
                    }
                }
            }
        }

        if(!error) {
            for(int i=0; i<2; i++) {
                if(index[i]==1) {
                    interfazBD.actualizarUsuairo((i+2), usr[i], pas[i]);
                }
            }
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.msgGuardado), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

}
