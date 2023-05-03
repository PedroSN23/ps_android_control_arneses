package com.example.ps_android_control_arneses.view.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ps_android_control_arneses.R;

import java.util.Locale;

public class AboutFragment extends Fragment {

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_about, container, false);

        String ver="";
        try {
            ver = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView tv = v.findViewById(R.id.versionName);
        tv.setText(String.format(Locale.getDefault(), "%s V%s", getResources().getString(R.string.app_name), ver));

        return v;
    }
}
