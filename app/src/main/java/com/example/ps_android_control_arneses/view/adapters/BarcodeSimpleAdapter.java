package com.example.ps_android_control_arneses.view.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ps_android_control_arneses.R;

import java.util.ArrayList;
import java.util.List;

public class BarcodeSimpleAdapter extends BaseAdapter {
    private final List<String> barcodes = new ArrayList<>();
    private final Activity activity;

    public BarcodeSimpleAdapter(Activity activity) {
        this.activity = activity;
    }

    public boolean addBarCode(String bc) {
        for(String b: barcodes) {
            if(b.compareTo(bc)==0) {
                return false;
            }
        }
        barcodes.add(bc);
        notifyDataSetChanged();
        return true;
    }

    @Override
    public int getCount() {
        return barcodes.size();
    }

    @Override
    public Object getItem(int i) {
        return barcodes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View rowView = convertView;
        TextView tv;
        if(rowView==null) {
            rowView = activity.getLayoutInflater().inflate(R.layout.list_barcode_adapter, parent, false);
            tv = rowView.findViewById(R.id.bcText);
            rowView.setTag(tv);
        } else {
            tv = (TextView) rowView.getTag();
        }
        tv.setText(barcodes.get(i));
        return rowView;
    }

    public void clearList() {
        barcodes.clear();
        notifyDataSetChanged();
    }
}
