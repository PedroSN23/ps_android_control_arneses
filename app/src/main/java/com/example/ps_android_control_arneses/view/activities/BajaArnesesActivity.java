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
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.controller.rfid.CF;
import com.example.ps_android_control_arneses.controller.rfid.CFL;
import com.example.ps_android_control_arneses.controller.rfid.RFIDActivityHandlerListener;
import com.example.ps_android_control_arneses.controller.rfid.RFIDControllActivity;
import com.example.ps_android_control_arneses.controller.rfid.TagsRead;
import com.example.ps_android_control_arneses.model.database.InterfazBD;
import com.example.ps_android_control_arneses.view.fragments.HeaderFragment;
import com.example.ps_android_control_arneses.view.fragments.StatusbarFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BajaArnesesActivity extends RFIDControllActivity {
    private RelativeLayout progreso;
    private static ArrayList<TagsRead> tagsReadArr = null;
    private MySimpleArrayAdapter mAdapter;

    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    private HeaderFragment headerFragment;
    private CF controlsFragment;
    private StatusbarFragment statusbarFragment;
    private InterfazBD interfazBD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alta_layout);

        interfazBD = new InterfazBD(this);
        headerFragment = new HeaderFragment(tituloActivity, style_enum.getColor(), style_enum.getColorTxt());
        headerFragment.addHeaderFragmentListner(this::onBackPressed);
        controlsFragment = new CF(style_enum, getResources().getString(R.string.eraser), getResources().getString(R.string.butbaja));
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
                if (tagsReadArr.isEmpty()) {
                    // Mostrar un Toast si la lista está vacía
                    Toast.makeText(getApplicationContext(), "ERROR: DATOS VACIOS", Toast.LENGTH_SHORT).show();
                } else {
                    boolean insert = interfazBD.bajaInventario(tagsReadArr);
                    if (insert) {
                        Toast.makeText(getApplicationContext(), "LOS DATOS SE DIERON DE BAJA", Toast.LENGTH_SHORT).show();
                        tagsReadArr.clear();
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR: FALLO AL DAR DE BAJA LOS DATOS", Toast.LENGTH_SHORT).show();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void button2ClickedSecond() {
            }
        });
        statusbarFragment = new StatusbarFragment(style_enum.getButUp());

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
                myThread.setPotencia(10);
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
                ArrayList<String[]> inventario = interfazBD.obtenerInventario();
                boolean newTag = false;
                if (epcs != null) {
                    for (String epc : epcs) {
                        boolean found = false;
                        boolean isValid = false;
                        for (String[] arnes : inventario) {
                            if (epc.equals(arnes[0])) {
                                found = true;
                                if (Integer.parseInt(arnes[3]) != 0) { // Verificar el valor de la columna de estado
                                    isValid = true;
                                }
                                break;
                            }
                        }
                        if (found && isValid) {
                            boolean tagExists = false;
                            for (TagsRead tag : tagsReadArr) {
                                if (tag.getEpc().equals(epc)) {
                                    tagExists = true;
                                    tag.addCount();
                                    break;
                                }
                            }
                            if (!tagExists) {
                                newTag = true;
                                // Obtener la fecha y la hora actual
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String fecha = sdf.format(new Date());
                                tagsReadArr.add(0, new TagsRead(epc, fecha, "EN PRESTAMO", 0));
                            }
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (newTag) {
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
            super(context, R.layout.altas_y_bajas, tagsRead);
            this.tagsRead = tagsRead;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            ViewHolder viewHolder;
            if(rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.altas_y_bajas, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.epcText = rowView.findViewById(R.id.epcText);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            System.out.println(tagsRead.get(position).getEpc());

            viewHolder.epcText.setText(String.format(Locale.getDefault(), "%s", tagsRead.get(position).getEpc()));
            return rowView;
        }
    }

}
