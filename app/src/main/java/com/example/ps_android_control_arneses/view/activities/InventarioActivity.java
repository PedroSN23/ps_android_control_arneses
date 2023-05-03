package com.example.ps_android_control_arneses.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.controller.rfid.CF;
import com.example.ps_android_control_arneses.controller.rfid.CFL;
import com.example.ps_android_control_arneses.controller.rfid.RFIDActivityHandlerListener;
import com.example.ps_android_control_arneses.controller.rfid.RFIDControllActivity;
import com.example.ps_android_control_arneses.controller.rfid.TagsRead;
import com.example.ps_android_control_arneses.view.fragments.HeaderFragment;
import com.example.ps_android_control_arneses.view.fragments.StatusbarFragment;

import java.util.ArrayList;
import java.util.Locale;

public class InventarioActivity extends RFIDControllActivity {
    private RelativeLayout progreso;
    private TextView cantidadTotal;
    private static ArrayList<TagsRead> tagsReadArr = null;
    private MySimpleArrayAdapter mAdapter;

    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    private HeaderFragment headerFragment;
    private CF controlsFragment;
    private StatusbarFragment statusbarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_layout);

        headerFragment = new HeaderFragment(tituloActivity, style_enum.getColor(), style_enum.getColorTxt());
        headerFragment.addHeaderFragmentListner(this::onBackPressed);
        controlsFragment = new CF(style_enum, getResources().getString(R.string.broom), getResources().getString(R.string.butClean));
        controlsFragment.addCFL(new CFL() {
            @Override
            public void lecturarToggleClicked() {
                if(isRfidReady.get()) {
                    if(!toggleBut) {
                        toggleBut=true;
                        startLectura();
                    } else {
                        stopLectura();
                        toggleBut = false;
                    }
                }
            }

            @Override
            public void button2Clicked() {
                tagsReadArr.clear();
                cantidadTotal.setText(String.format(Locale.getDefault(),"%d", tagsReadArr.size()));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void button2ClickedSecond() {
            }
        });
        statusbarFragment = new StatusbarFragment(style_enum.getButUp()); //bundle.getInt("drawable"));

        progreso = findViewById(R.id.menu3ProgresoRfid);

        if(transaction.isEmpty()) {
            transaction.add(R.id.headerFragment, headerFragment);
            transaction.add(R.id.controlsFragment, controlsFragment);
            transaction.add(R.id.statusbarFragment, statusbarFragment).commit();
        } else {
            transaction = null;
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.headerFragment, headerFragment);
            transaction.replace(R.id.controlsFragment, controlsFragment);
            transaction.replace(R.id.fragment_content, statusbarFragment).commit();
        }

        tagsReadArr = new ArrayList<>();

        ListView mList = findViewById(R.id.menu3ListRfid);
        mAdapter = new MySimpleArrayAdapter(this, tagsReadArr);
        System.out.println("DESDE MENU3 "+ tagsReadArr.size());
        mList.setAdapter(mAdapter);
    }

    @Override
    public void setAdapterHandler() {
        rfidActivityHandler.addRFIDActivityHandlerAdapter(new RFIDActivityHandlerListener() {
            @Override
            public void setError(String msg) {
                headerFragment.elevateMainBlock((float) 54.0);
                isRfidReady.set(false);
                progreso.setVisibility(View.GONE);
                statusbarFragment.setStatusIcon(-1);
                statusbarFragment.setTextMessage(msg);
            }

            @Override
            public void iniciarSuccess() {
                myThread.setPotencia(interfazBD.obtenerPotencia());
                statusbarFragment.setStatusIcon(1);
                statusbarFragment.setTextMessage("RFID Conectado");
            }

            @Override
            public void instanceSuccess() {
                headerFragment.elevateMainBlock((float) 0.0);
                progreso.setVisibility(View.VISIBLE);
                myThread.start();
            }

            @Override
            public void changePowerSuccess() {
                isRfidReady.set(true);
                headerFragment.elevateMainBlock((float) 54.0);
                progreso.setVisibility(View.GONE);
                soundPowerChanged();
            }

            @Override
            public void lecturaIniciada() {
                statusbarFragment.setTextMessage("RFID leyendo");
            }

            @Override
            public void lecturaFinalizada() {
                statusbarFragment.setTextMessage("RFID Conectado");
            }

            @Override
            public void threadReady() {
                myThread.iniciarRfid();
            }

            @Override
            public void finalizar() {
                isRfidReady.set(false);
                myThread.myThreadHandler.getLooper().quit();
                statusbarFragment.setTextMessage("Conectando a RFID");
                statusbarFragment.setStatusIcon(0);
            }

            @Override
            public void tagsLecturaInventario(ArrayList<String> stringArrayList) {

            }

            @Override
            public void tagsLecturaCiega(ArrayList<String> epcs, ArrayList<String> rssis) {
                boolean newTag = false;
                if(epcs != null) {
                    for (int i = 0; i < epcs.size(); ++i) {
                        boolean found = false;
                        for (int j = 0; j < tagsReadArr.size(); j++) {
                            if (tagsReadArr.get(j).getEpc().compareTo(epcs.get(i)) == 0) {
                                tagsReadArr.get(j).addCount();
                                double rsssDouble = 0.00;
                                try {
                                    if(rssis != null) {
                                        rsssDouble = Double.parseDouble(rssis.get(i));
                                    }
                                } catch (NullPointerException | NumberFormatException ex) {
                                    rsssDouble = 0.00;
                                }
                                tagsReadArr.get(j).setRssi(rsssDouble);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            newTag = true;
                            double rsssDouble = 0.00;
                            try {
                                if(rssis != null) {
                                    rsssDouble = Double.parseDouble(rssis.get(i));
                                }
                            } catch (NullPointerException | NumberFormatException ex) {
                                rsssDouble = 0.00;
                            }
                            tagsReadArr.add(0, new TagsRead(epcs.get(i)));
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                if(newTag) {
                    soundTagRead();
                }
            }

            @Override
            public void grabarSuccess() {

            }

            @Override
            public void enviarRssi(double aDouble) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        progreso.setVisibility(RelativeLayout.VISIBLE);
        headerFragment.elevateMainBlock((float) 0.0);
    }

    @Override
    public void startLectura() {
        System.out.println("START LECTURA");
        myThread.startLecturaCiega();
        controlsFragment.setButton1PRessed(true);
    }

    @Override
    public void stopLectura() {
        myThread.stopReading();
        controlsFragment.setButton1PRessed(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<TagsRead> {
        private class ViewHolder {
            TextView epcText;
        }

        private final ArrayList<TagsRead> tagsRead;

        MySimpleArrayAdapter(Context context, ArrayList<TagsRead> tagsRead) {
            super(context, R.layout.inventario_inflate, tagsRead);
            this.tagsRead = tagsRead;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            ViewHolder viewHolder;
            if(rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.inventario_inflate, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.epcText = rowView.findViewById(R.id.epcText);
                 rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            System.out.println(tagsRead.get(position).getEpc());

            viewHolder.epcText.setText(String.format(Locale.getDefault(), "EPC: %s", tagsRead.get(position).getEpc()));
            return rowView;
        }
    }

}
