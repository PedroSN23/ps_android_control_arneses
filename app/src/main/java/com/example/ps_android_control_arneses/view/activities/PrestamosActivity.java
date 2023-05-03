package com.example.ps_android_control_arneses.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.ps_android_control_arneses.controller.rfid.NewCFL;
import com.example.ps_android_control_arneses.controller.rfid.NewCF;
import com.example.ps_android_control_arneses.controller.rfid.RFIDActivityHandlerListener;
import com.example.ps_android_control_arneses.controller.rfid.RFIDControllActivity;
import com.example.ps_android_control_arneses.controller.rfid.TagsRead;
import com.example.ps_android_control_arneses.view.fragments.HeaderFragment;
import com.example.ps_android_control_arneses.view.fragments.StatusbarFragment;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PrestamosActivity extends RFIDControllActivity {
    private RelativeLayout progreso;
    private static ArrayList<TagsRead> tagsReadArr = null;
    private MySimpleArrayAdapter mAdapter;

    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    private HeaderFragment headerFragment;
    private NewCF controlsFragment;
    private StatusbarFragment statusbarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prestamo_layout);

        headerFragment = new HeaderFragment(tituloActivity, style_enum.getColor(), style_enum.getColorTxt());
        headerFragment.addHeaderFragmentListner(this::onBackPressed);
        controlsFragment = new NewCF(style_enum, getResources().getString(R.string.file_excel), getResources().getString(R.string.archivo_excel), getResources().getString(R.string.door_open), getResources().getString(R.string.prestar));
        controlsFragment.addCFL(new NewCFL() {
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
                ArrayList<String[]> inventario = interfazBD.obtenerInventario();

                if (inventario.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "El inventario está vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Filtrar los datos que tienen un estado igual a 2
                ArrayList<String[]> inventarioPrestamos = new ArrayList<>();
                for (String[] filaDatos : inventario) {
                    if (filaDatos[3].equals("2")) {
                        inventarioPrestamos.add(filaDatos);
                    }
                }

                if (inventarioPrestamos.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No hay préstamos registrados", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear el libro de Excel y la hoja
                XSSFWorkbook libro = new XSSFWorkbook();
                XSSFSheet hoja = libro.createSheet("Inventario");

                // Crear la fila de encabezados
                Row filaEncabezados = hoja.createRow(0);
                filaEncabezados.createCell(0).setCellValue("EPC");
                filaEncabezados.createCell(1).setCellValue("Fecha Alta");
                filaEncabezados.createCell(2).setCellValue("Fecha Última Lectura");
                filaEncabezados.createCell(3).setCellValue("Estado");

                // Agregar los datos
                int filaActual = 1;
                for (String[] filaDatos : inventarioPrestamos) {
                    Row fila = hoja.createRow(filaActual++);
                    fila.createCell(0).setCellValue(filaDatos[0]);
                    fila.createCell(1).setCellValue(filaDatos[1]);
                    fila.createCell(2).setCellValue(filaDatos[2]);
                    fila.createCell(3).setCellValue("En Prestamo");
                }

                try {
                    // Obtener la ruta de la carpeta de descargas
                    File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                    // Crear el archivo y guardarlo en la carpeta de descargas
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String fechaActual = sdf.format(new Date());
                    String nombreArchivo = "InventarioPrestamos_" + fechaActual + ".xlsx";
                    File archivo = new File(downloadDir, nombreArchivo);
                    FileOutputStream outputStream = new FileOutputStream(archivo);
                    libro.write(outputStream);
                    outputStream.close();

                    // Mostrar un mensaje indicando la ruta del archivo guardado
                    Toast.makeText(getApplicationContext(), "El archivo se guardó en " + archivo.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "No se pudo guardar el archivo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void button2ClickedSecond() {

            }

            @Override
            public void button3Clicked() {
                if (tagsReadArr.isEmpty()) {
                    // Mostrar un Toast si la lista está vacía
                    Toast.makeText(getApplicationContext(), "ERROR:NO SE HA LEIDO NINGUN ARNES", Toast.LENGTH_SHORT).show();
                } else {
                    boolean insert = interfazBD.actualizarEstado(tagsReadArr);
                    if (insert) {
                        Toast.makeText(getApplicationContext(), "PRESTAMO EXITOSO", Toast.LENGTH_SHORT).show();
                        tagsReadArr.clear();
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR, NO SE LOGRO HACER EL PRESTAMO", Toast.LENGTH_SHORT).show();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void button3ClickedSecond() {
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

        ListView mList = findViewById(R.id.lista_prestamo);
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
                ArrayList<String[]> inventario = interfazBD.obtenerInventario();
                boolean newTag = false;
                if (epcs != null) {
                    for (String epc : epcs) {
                        boolean found = false;
                        boolean isValid = false;
                        for (String[] arnes : inventario) {
                            if (epc.equals(arnes[0])) {
                                found = true;
                                if (arnes[3].equals("1")) {
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
                                tagsReadArr.add(0, new TagsRead(epc, fecha, "DISPONIBLE",2));
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
            TextView estadoText;
        }

        private final ArrayList<TagsRead> tagsRead;

        MySimpleArrayAdapter(Context context, ArrayList<TagsRead> tagsRead) {
            super(context, R.layout.prestamos_inflate, tagsRead);
            this.tagsRead = tagsRead;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            ViewHolder viewHolder;
            if(rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.prestamos_inflate, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.epcText = rowView.findViewById(R.id.epcText);
                viewHolder.estadoText = rowView.findViewById(R.id.estadoText);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            System.out.println(tagsRead.get(position).getEpc());
            viewHolder.epcText.setText(String.format(Locale.getDefault(), "%s", tagsRead.get(position).getEpc()));
            viewHolder.estadoText.setText(String.format(Locale.getDefault(), "%s",tagsRead.get(position).getTexto()));
            return rowView;
        }
    }

}
