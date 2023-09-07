package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Confirmar_reserva extends AppCompatActivity {

    TextView saldo, costo, restante;

    DatabaseReference reference;
    Button confirmar, cancelar;
    FirebaseAuth auth;
    FirebaseUser user;
    String uid,cod,placa,tiempo,fecha;
    int cos,resta,sal ;
    Double lat,lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_reserva);

        saldo=(TextView)findViewById(R.id.saldoreserva);
        costo=(TextView)findViewById(R.id.costpreserva);
        restante=(TextView)findViewById(R.id.nuevosaldo);
        confirmar=(Button)findViewById(R.id.confirmar);
        reference= FirebaseDatabase.getInstance().getReference();
        lat=getIntent().getExtras().getDouble("lat");
        lon=getIntent().getExtras().getDouble("lon");
        cod=getIntent().getExtras().getString("cod");
        placa=getIntent().getExtras().getString("placa");
        cos=getIntent().getExtras().getInt("costo");
        tiempo=getIntent().getExtras().getString("tiempo");
        fecha=getIntent().getExtras().getString("fecha");
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        uid=user.getUid();
        costo.setText(String.valueOf(cos));
        reference.child("app").child("usuarios").child(uid).child("saldo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    sal=Integer.parseInt(dataSnapshot.getValue().toString());
                    saldo.setText(dataSnapshot.getValue().toString());
                    resta = sal-cos;
                    if(resta>=0){
                        restante.setText(String.valueOf(resta));
                    }else{
                        restante.setText("su saldo es insuficiente");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Calendar calendar=Calendar.getInstance();
                if(tiempo.equals("1/2"))
                {
                    calendar.add(Calendar.MINUTE,30);
                }else {
                    calendar.add(Calendar.HOUR,cos/5);
                }

                Date date = calendar.getTime();
                //Date date1 =Calendar.getInstance().getTime().;
                String currentDateandTime = simpleDateFormat.format(date);
                String currentDateandTime1 = simpleDateFormat.format(new Date());



                Map<String,Object> map=new HashMap<>();
                map.put("disponibilidad","ocupado");
                map.put("usuario",uid);
                map.put("placa",placa);
                map.put("llegada","no");
                map.put("HoraFin",currentDateandTime);
                map.put("HoraInicio",currentDateandTime1);
                Map<String,Object> map1=new HashMap<>();
                map1.put("NuevoSaldo",resta);
                map1.put("SaldoAntiguo",sal);
                map1.put("costo",cos);
                map1.put("fecha",fecha);
                map1.put("HoraInicio",currentDateandTime1);
                if(resta>=0) {

                    reference.child("app").child("parqueos").child(cod).child("diponibilidad").setValue(map);
                    reference.child("app").child("usuarios").child(uid).child("reserva").setValue(cod);
                    reference.child("app").child("usuarios").child(uid).child("saldo").setValue(String.valueOf(resta));
                    reference.child("app").child("usuarios").child(uid).child("gastos").push().setValue(map1);
                    setConfirmar();

                }else{

                    cancelar();

                }



            }
        });



       }
       public  void setConfirmar(){


           AlertDialog.Builder alert=new AlertDialog.Builder(Confirmar_reserva.this);
           alert.setMessage("Exito").setPositiveButton("ok", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

                   finish();


               }
           });
           AlertDialog dialog=alert.create();
           dialog.setTitle("Exito");
           dialog.show();


       }
    public void cancelar(){

        AlertDialog.Builder alert=new AlertDialog.Builder(Confirmar_reserva.this);
        alert.setMessage("asegurece de tener suficiente credito para realizar esta accion").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();


            }
        });
        AlertDialog dialog=alert.create();
        dialog.setTitle("Saldo insuficiente");
        dialog.show();

    }
}
