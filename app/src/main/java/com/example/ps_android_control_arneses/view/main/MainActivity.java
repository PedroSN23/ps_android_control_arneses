package com.example.ps_android_control_arneses.view.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.ps_android_control_arneses.R;
import com.example.ps_android_control_arneses.model.clases.Usuarios;
import com.example.ps_android_control_arneses.model.database.InterfazBD;
import com.example.ps_android_control_arneses.view.clases.FontAwesome;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST = 1000;

    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    private boolean aplied = false;
    private boolean animStart = false;
    private Menu_Enum enumPressed;
    private int index = 0;
    private boolean frag_on = false;
    private final MenuFragment[] fragment = new MenuFragment[4];
    private Animation animSubBut;
    private Window window;
    private Context context;
    private InterfazBD interfazBD;

    private RelativeLayout progresoRv;
    private TextView progresoTv;

    private final RelativeLayout[] menu=new RelativeLayout[4];
    private final FontAwesome[] menuicon=new FontAwesome[4];

    private Animation animResize;
    private final Animation[] animBack = new Animation[4];
    private final Animation[] animOrig = new Animation[4];

    private Usuarios usuarios;

    public MainActivity() {
        fragment[0]=new MenuFragment(R.layout.menu1_fragment);
        fragment[1]=new MenuFragment(R.layout.menu2_fragment);
        fragment[2]=new MenuFragment(R.layout.menu3_fragment);
        fragment[3]=new MenuFragment(R.layout.menu4_fragment);
    }

    @SuppressLint({"ClickableViewAccessibility", "HardwareIds"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity_main);
        context = this;
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(context, R.color.menu1p));

        boolean found = false;
        Log.d("SERIAL", Build.SERIAL);
        String[] seriales = getResources().getStringArray(R.array.seriales);
        for(String s: seriales) {
            if(s.compareTo(Build.SERIAL)==0) {
                found = true;
                break;
            }
        }
        if(!found) {
            MainActivity.this.finish();
            System.exit(0);
        }

        context = this;

        interfazBD = new InterfazBD(context);

        final Animation animInvTop = AnimationUtils.loadAnimation(this, R.anim.menu1_anim_top);
        final Animation animInvGone = AnimationUtils.loadAnimation(this, R.anim.menu1_anim_gone);
        animBack[0] = AnimationUtils.loadAnimation(this, R.anim.menu1_anim_back);
        animOrig[0] = AnimationUtils.loadAnimation(this, R.anim.menu1_anim_orig);
        final Animation animBuscTop = AnimationUtils.loadAnimation(this, R.anim.menu2_anim_top);
        final Animation animBuscGone = AnimationUtils.loadAnimation(this, R.anim.menu2_anim_gone);
        animBack[1] = AnimationUtils.loadAnimation(this, R.anim.menu2_anim_back);
        animOrig[1] = AnimationUtils.loadAnimation(this, R.anim.menu2_anim_orig);
        final Animation animAddTop = AnimationUtils.loadAnimation(this, R.anim.menu3_anim_top);
        final Animation animAddGone = AnimationUtils.loadAnimation(this, R.anim.menu3_anim_gone);
        animBack[2] = AnimationUtils.loadAnimation(this, R.anim.menu3_anim_back);
        animOrig[2] = AnimationUtils.loadAnimation(this, R.anim.menu3_anim_orig);
        final Animation animConfTop = AnimationUtils.loadAnimation(this, R.anim.menu4_anim_top);
        final Animation animConfGone = AnimationUtils.loadAnimation(this, R.anim.menu4_anim_gone);
        animBack[3] = AnimationUtils.loadAnimation(this, R.anim.menu4_anim_back);
        animOrig[3] = AnimationUtils.loadAnimation(this, R.anim.menu4_anim_orig);
        final Animation animShrink = AnimationUtils.loadAnimation(this, R.anim.anim_scale_icon);
        animResize = AnimationUtils.loadAnimation(this, R.anim.anim_scal_icon_inv);
        animSubBut = AnimationUtils.loadAnimation(this, R.anim.anim_sub_buttons);

        animSubBut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if(v!=null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(240, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        v.vibrate(240);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(enumPressed.checkPermisos(usuarios.getRol())==1) {
                    if(enumPressed.isVerify()) {
                        crearDialogoAutentificar(enumPressed, false);
                    } else {
                        if(enumPressed.isActivity()) {
                            iniciarActivity(enumPressed);
                        } else {
                            try {
                                @SuppressWarnings("ConfusingArgumentToVarargsMethod") Method method = MainActivity.this.getClass().getMethod(enumPressed.getClassName(), null);
                                //noinspection ConfusingArgumentToVarargsMethod
                                method.invoke(MainActivity.this, null);
                            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "El Usuario: "+usuarios.getUsuario()+" no tiene permisos", Toast.LENGTH_LONG).show();
                }
                animStart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animShrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!frag_on) {
                    switch(index) {
                        case 0:
                            window.setStatusBarColor(ContextCompat.getColor(context, R.color.menu1p));
                            menu[0].setElevation((float)54.0);
                            frag_on=true;
                            if(transaction.isEmpty()) {
                                transaction.add(R.id.fragment_content, fragment[0]);
                            } else {
                                transaction = null;
                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_content, fragment[0]);
                            }
                            transaction.commit();
                            break;
                        case 1: //
                            window.setStatusBarColor(ContextCompat.getColor(context, R.color.menu2p));
                            menu[1].setElevation((float)54.0);
                            frag_on=true;
                            if(transaction.isEmpty()) {
                                transaction.add(R.id.fragment_content, fragment[1]);
                            } else {
                                transaction = null;
                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_content, fragment[1]);
                            }
                            transaction.commit();
                            break;
                        case 2:
                            window.setStatusBarColor(ContextCompat.getColor(context, R.color.menu3p));
                            menu[2].setElevation((float)54.0);
                            frag_on=true;
                            if(transaction.isEmpty()) {
                                transaction.add(R.id.fragment_content, fragment[2]);
                            } else {
                                transaction = null;
                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_content, fragment[2]);
                            }
                            transaction.commit();
                            break;
                        case 3:
                            window.setStatusBarColor(ContextCompat.getColor(context, R.color.menu4p));
                            menu[3].setElevation((float)54.0);
                            frag_on=true;
                            if(transaction.isEmpty()) {
                                transaction.add(R.id.fragment_content, fragment[3]);
                            } else {
                                transaction = null;
                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_content, fragment[3]);
                            }
                            transaction.commit();
                            break;
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animResize.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                window.setStatusBarColor(ContextCompat.getColor(context, R.color.menu1p));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        menu[0] = findViewById(R.id.menu1Main);
        menu[1] = findViewById(R.id.menu2Main);
        menu[2] = findViewById(R.id.menu3Main);
        menu[3] = findViewById(R.id.menu4Main);
        menuicon[0] = findViewById(R.id.men1Ico);
        menuicon[1] = findViewById(R.id.men2Ico);
        menuicon[2] = findViewById(R.id.men3Ico);
        menuicon[3] = findViewById(R.id.men4Ico);

        progresoRv = findViewById(R.id.progresoRv);
        progresoTv = findViewById(R.id.progresoTv);

        menu[0].setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if(!aplied) {
                    index=0;
                    aplied=true;
                    view.startAnimation(animInvTop);
                    menu[1].startAnimation(animBuscGone);
                    menu[2].startAnimation(animAddGone);
                    menu[3].startAnimation(animConfGone);
                    for(int i=0; i<4; i++) {
                        menuicon[i].startAnimation(animShrink);
                    }
                } else {
                    if(motionEvent.getY()<=300.0) {
                        rutina_regresar();
                    }
                }
                return false;
            }
            return true;
        });

        menu[1].setOnClickListener(view -> {
            if(!aplied) {
                index=1;
                aplied=true;
                menu[0].startAnimation(animInvGone);
                view.startAnimation(animBuscTop);
                menu[2].startAnimation(animAddGone);
                menu[3].startAnimation(animConfGone);
                for(int i=0; i<4; i++) {
                    menuicon[i].startAnimation(animShrink);
                }
            }
        });

        menu[2].setOnClickListener(view -> {
            if(!aplied) {
                index=2;
                aplied=true;
                menu[0].startAnimation(animInvGone);
                menu[1].startAnimation(animBuscGone);
                view.startAnimation(animAddTop);
                menu[3].startAnimation(animConfGone);
                for(int i=0; i<4; i++) {
                    menuicon[i].startAnimation(animShrink);
                }
            }
        });

        menu[3].setOnClickListener(view -> {
            if(!aplied) {
                index=3;
                aplied=true;
                menu[0].startAnimation(animInvGone);
                menu[1].startAnimation(animBuscGone);
                menu[2].startAnimation(animAddGone);
                view.startAnimation(animConfTop);
                for(int i=0; i<4; i++) {
                    menuicon[i].startAnimation(animShrink);
                }
            }
        });

        enumPressed = Menu_Enum.inicio;

        onChecarPermisos();

        crearDialogoAutentificar(enumPressed, true);
    }

    private void onChecarPermisos() {
        ArrayList<String> lista = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            lista.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            lista.add(Manifest.permission.CAMERA);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            lista.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            lista.add(Manifest.permission.VIBRATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            lista.add(Manifest.permission.INTERNET);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            lista.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            lista.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(lista.size()>0) {
            String[] strings = new String[lista.size()];
            for(int i=0; i<lista.size(); i++) {
                strings[i]=lista.get(i);
            }
            ActivityCompat.requestPermissions(MainActivity.this, strings, MY_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                MainActivity.this.finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        interfazBD.close();
        super.onDestroy();
    }
    @SuppressWarnings("unused")


    private void iniciarActivity(Menu_Enum enumPressed) {
        try {
            Intent intent = new Intent(context, Class.forName("com.example.ps_android_control_arneses.view.activities." + enumPressed.getClassName()));
            intent.putExtra("id", enumPressed.getId());
            intent.putExtra("dir", enumPressed.getDir());
            intent.putExtra("texto", enumPressed.getTexto());
            intent.putExtra("styleCode", enumPressed.getStyle_enum().getCode());
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void rutina_regresar() {
        aplied = false;
        if(index!=0) {
            int result = (-18)*index+54;
            menu[index].setElevation((float)result);
        }
        if(frag_on) {
            frag_on=false;
            if(!transaction.isEmpty()) {
                transaction=null;
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(fragment[index]).commit();
            }
        }
        for(int i=0; i<4; i++) {
            if(i==index) {
                menu[i].startAnimation(animOrig[i]);
            } else {
                menu[i].startAnimation(animBack[i]);
            }
        }
        for(int i=0; i<4; i++) {
            menuicon[i].startAnimation(animResize);
        }
    }

    @Override
    public void onBackPressed() {
        if(aplied) {
            rutina_regresar();
        } else {
            super.onBackPressed();
            System.exit(0);
        }
    }

    public void FragmentClicked(View view) {
        if(!animStart) {
            animStart=true;
            view.startAnimation(animSubBut);
            enumPressed= (Menu_Enum) view.getTag();
        }
    }
    private void crearDialogoAutentificar(final Menu_Enum menu_enum, final boolean inicio) {
        View promptsView = View.inflate(MainActivity.this, R.layout.dialog_autentificar_administrador, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(promptsView)
                .create();
        final EditText usuario = promptsView.findViewById(R.id.admUser);
        if(this.usuarios!=null && this.usuarios.getId()>=0) {
            usuario.setText(this.usuarios.getUsuario());
            usuario.setKeyListener(null);
        }
        final EditText password = promptsView.findViewById(R.id.admPass);
        Button button1 = promptsView.findViewById(R.id.button1);
        button1.setBackground(ContextCompat.getDrawable(context, menu_enum.getStyle_enum().getButDrawable()));
        Button button2 = promptsView.findViewById(R.id.button2);
        button2.setBackground(ContextCompat.getDrawable(context, menu_enum.getStyle_enum().getButDrawable()));
        alertDialog.setOnShowListener(dialogInterface -> {
            button1.setOnClickListener(view -> {
                String user = usuario.getText().toString();
                String pass = password.getText().toString();
                if (user.length() > 0 && pass.length() > 0) {
                    if(inicio) {
                        this.usuarios = new Usuarios(user, pass);
                    } else {
                        this.usuarios.setContrasena(pass);
                    }
                    if(interfazBD.iniciarSesion(this.usuarios)) {
                        if(inicio) {
                            alertDialog.dismiss();
                        } else {
                            if (usuarios.getRol() < 2) {
                                iniciarActivity(menu_enum);
                            } else {
                                Toast.makeText(MainActivity.this, "Sólo el administrador tiene acceso a esta función", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Usuario y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "El usuario y la contraseña no deben estar vacìos", Toast.LENGTH_SHORT).show();
                }
                if(!inicio) {
                    alertDialog.dismiss();
                }
            });
            button2.setOnClickListener(view -> {
                if(inicio) {
                    MainActivity.this.finish();
                    System.exit(0);
                } else {
                    alertDialog.dismiss();
                }
            });
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @SuppressWarnings("unused")
    public void butonClicked(int buttonClicked) {
        if(buttonClicked==0) {
            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis() + 100;
            float x = 0.0f;
            float y = 0.0f;
            int metaState = 0;
            MotionEvent motionEvent = MotionEvent.obtain(
                    downTime,
                    eventTime,
                    MotionEvent.ACTION_UP,
                    x,
                    y,
                    metaState
            );
            menu[0].dispatchTouchEvent(motionEvent);
        } else {
            menu[buttonClicked].performClick();
        }
    }

    @SuppressWarnings("unused")
    public void butonClickedFragment(int submenu, int buttonClicked) {
        if(submenu<fragment.length && fragment[submenu]!=null) {
            fragment[submenu].buttonClicked(buttonClicked-1);
        }
    }

    @SuppressWarnings("unused")
    public void returnToMain() {
        if(aplied) {
            rutina_regresar();
        }
    }

    public void updateProgress(Integer value) {
        progresoTv.setText(String.format(Locale.getDefault(), "%d%c", value, '%'));
    }

    public void progressVisible(boolean b) {
        if(b) {
            progresoTv.setText("0%");
            progresoRv.setVisibility(View.VISIBLE);
        } else {
            progresoRv.setVisibility(View.GONE);
        }
    }

    public void setErrorMessge(String errMsg) {
        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
    }

    public void setExportSuccess() {
        Toast.makeText(this, "ARCHIVO EXPORTADO", Toast.LENGTH_LONG).show();
    }
}