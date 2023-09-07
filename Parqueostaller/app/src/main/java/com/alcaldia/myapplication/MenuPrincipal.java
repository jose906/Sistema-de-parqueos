package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.Duration;

public class MenuPrincipal extends AppCompatActivity {

    Button mapa,regis,credito,settings,perfil,listas;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        reference= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        uid=user.getUid();


        mapa=(Button)findViewById(R.id.mapa);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),com.alcaldia.myapplication.MapsActivity.class);
                startActivity(intent);
            }
        });
        regis=(Button)findViewById(R.id.btnregistro);
        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),com.alcaldia.myapplication.Vehiculos.class);
                startActivity(intent);

            }
        });
        credito=(Button)findViewById(R.id.credito);
        credito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),com.alcaldia.myapplication.Saldo.class);
                startActivity(intent);
            }
        });
        settings=(Button)findViewById(R.id.configuracion);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),com.alcaldia.myapplication.SettingsActivity.class);
                startActivity(intent);
            }
        });
        perfil=(Button)findViewById(R.id.perfil);
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),com.alcaldia.myapplication.Perfil.class);
                startActivity(intent);
            }
        });
        listas = (Button)findViewById(R.id.reservas);
        listas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),com.alcaldia.myapplication.ListaReservas.class);
                startActivity(intent);

            }
        });

        nollega();
    }


    public void nollega(){

        reference.child("app").child("usuarios").child(uid).child("reserva").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue().toString().equals("no")){



                }else {


                    cancelacion(dataSnapshot.getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public  void cancelacion(final String id){

        reference.child("app").child("parqueos").child(id).child("diponibilidad").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){



                if (dataSnapshot.child("llegada").getValue().toString().equals("no")){

                    try {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        Date date = simpleDateFormat.parse(dataSnapshot.child("HoraInicio").getValue().toString());
                        String currentDateandTime = simpleDateFormat.format(new Date());
                        Date date1 = simpleDateFormat.parse(currentDateandTime);
                        //Toast.makeText(MenuPrincipal.this, dataSnapshot.child("HoraInicio").getValue().toString(), Toast.LENGTH_SHORT).show();
                        long difference = date1.getTime() - date.getTime();
                        long difference_In_Minutes = (difference / (1000 * 60));
                        if (difference_In_Minutes > 15){

                            Map<String,Object> map=new HashMap<>();
                            map.put("disponibilidad","disponible");
                            map.put("usuario","no");
                            map.put("placa","no");
                            map.put("llegada","no");
                            map.put("HoraFin","00:00");
                            map.put("HoraInicio","00:00");

                            reference.child("app").child("parqueos").child(id).child("diponibilidad").setValue(map);
                            reference.child("app").child("usuarios").child(uid).child("reserva").setValue("no");
                            AlertDialog.Builder alert=new AlertDialog.Builder(MenuPrincipal.this);
                            alert.setMessage("El usuario no llego en el tiempo establecido").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {




                                }
                            });
                            AlertDialog dialog=alert.create();
                            dialog.setTitle("Reserva cancelada");
                            dialog.show();

                        }

                    }catch (Exception e){



                    }

                }else {

                    try {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        Date date = simpleDateFormat.parse(dataSnapshot.child("HoraFin").getValue().toString());
                        String currentDateandTime = simpleDateFormat.format(new Date());
                        Date date1 = simpleDateFormat.parse(currentDateandTime);
                        //Toast.makeText(MenuPrincipal.this, dataSnapshot.child("HoraInicio").getValue().toString(), Toast.LENGTH_SHORT).show();
                        long difference = date1.getTime() - date.getTime();
                        long difference_In_Minutes = (difference / (1000 * 60));

                        if (difference_In_Minutes > 30){

                            Map<String,Object> map=new HashMap<>();
                            map.put("disponibilidad","disponible");
                            map.put("usuario","no");
                            map.put("placa","no");
                            map.put("llegada","no");
                            map.put("HoraFin","00:00");
                            map.put("HoraInicio","00:00");

                            reference.child("app").child("parqueos").child(id).child("diponibilidad").setValue(map);
                            reference.child("app").child("usuarios").child(uid).child("reserva").setValue("no");
                            AlertDialog.Builder alert=new AlertDialog.Builder(MenuPrincipal.this);
                            alert.setMessage("El usuario no anuncio su salida en el tiempo establecido se establecera una multa de 10bs y se procede a cancelar su reserva").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {




                                }
                            });
                            AlertDialog dialog=alert.create();
                            dialog.setTitle("Tiempo finalizo!");
                            dialog.show();

                        }

                    }catch (Exception e){

                        Log.e("hola",e.getMessage());

                    }





                }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
