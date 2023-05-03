package com.example.ps_android_control_arneses.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.view.adapters.BarcodeSimpleAdapter;

public class BarcodeLecturasFragment extends Fragment {
    private BarcodeSimpleAdapter barcodeSimpleAdapter;

    public BarcodeLecturasFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_barcode, container, false);
        ListView listView = v.findViewById(R.id.listview);
        barcodeSimpleAdapter = new BarcodeSimpleAdapter(getActivity());
        listView.setAdapter(barcodeSimpleAdapter);
        return v;
    }

    public boolean addBarcode(String bc) {
        return barcodeSimpleAdapter.addBarCode(bc);
    }

    public void clearList() {
        barcodeSimpleAdapter.clearList();
    }
}