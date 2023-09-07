package com.alcaldia.contolador_taller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class menu extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView myscan;


    DatabaseReference reference;
    Button leerQr,porplaca,manual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        reference = FirebaseDatabase.getInstance().getReference();
        leerQr = (Button)findViewById(R.id.leerqr);
        leerQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan(view);
            }
        });

        porplaca = (Button)findViewById(R.id.leer_placa);
        porplaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), com.alcaldia.contolador_taller.Confirmacion_placa.class);
                startActivity(intent);
            }
        });
        manual = (Button)findViewById(R.id.btn_reserva);
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),com.alcaldia.contolador_taller.Reserva_manual.class);
                startActivity(intent);
            }
        });


    }

    public void scan(View view){
        myscan=new ZXingScannerView(this);
        setContentView(myscan);
        myscan.setResultHandler(this);
        myscan.startCamera();

    }


    @Override
    public void handleResult(com.google.zxing.Result result) {

        String id =  result.getText().toString();
        myscan.resumeCameraPreview(this);
        validar(id);
        myscan.stopCamera();

    }

    public void validar(final String carnet){


        reference.child("app").child("parqueos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot ds :dataSnapshot.getChildren()){


                       String a = ds.child("diponibilidad").child("usuario").getValue().toString();
                       if(carnet.equals(a)){
                           if(ds.child("diponibilidad").child("llegada").getValue().toString().equals("no")){

                           reference.child("app").child("parqueos").child(ds.getKey().toString()).child("diponibilidad").child("llegada").setValue("si");
                           verificar(ds.getKey().toString());


                           }else {

                               if(ds.child("diponibilidad").child("llegada").getValue().toString().equals("si")){

                                   Map<String,Object> map=new HashMap<>();
                                   map.put("disponibilidad","disponible");
                                   map.put("usuario","no");
                                   map.put("placa","no");
                                   map.put("llegada","no");
                                   map.put("HoraFin","00:00");
                                   map.put("HoraInicio","00:00");

                                   reference.child("app").child("parqueos").child(ds.getKey().toString()).child("diponibilidad").setValue(map);
                                   reference.child("app").child("usuarios").child(carnet).child("reserva").setValue("no");
                                   verificar(ds.getKey().toString());




                               }






                           }

                       }


                    }


                    if(dataSnapshot.getValue().toString().equals("no")){




                    }else {
                        //Intent intent = new Intent(getApplicationContext(), com.alcaldia.contolador_taller.Confirmacion.class);
                        //intent.putExtra("carnet", carnet);
                        //startActivity(intent);
                    }
                }
                else{
                    //Toast.makeText(menu.this, "no hay nada", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void verificar(String id){
        Intent intent = new Intent(getApplicationContext(),com.alcaldia.contolador_taller.Confirmacion.class);
        intent.putExtra("uid",id);
        startActivity(intent);

    }
}