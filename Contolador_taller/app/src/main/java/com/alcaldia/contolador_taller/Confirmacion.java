package com.alcaldia.contolador_taller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Confirmacion extends AppCompatActivity {


    DatabaseReference reference;
    TextView zona, calle, numero, inicio,fin, fecha, placa;
    String id;
    LinearLayout ll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion);

        zona = (TextView)findViewById(R.id.zona);
        calle = (TextView)findViewById(R.id.calle);
        numero = (TextView)findViewById(R.id.numero);
        inicio = (TextView)findViewById(R.id.fin);
        fin = (TextView)findViewById(R.id.fin);
        fecha = (TextView)findViewById(R.id.fecha);
        placa = (TextView)findViewById(R.id.placa);
        ll = (LinearLayout)findViewById(R.id.llconfirmacion);
         id = getIntent().getStringExtra("uid");
        reference = FirebaseDatabase.getInstance().getReference();
        getdata();
    }


    public void getdata(){

        reference.child("app").child("parqueos").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.exists()) {

                    zona.setText(snapshot.child("zona").getValue().toString());
                    calle.setText(snapshot.child("calle").getValue().toString());
                    numero.setText(snapshot.child("numero").getValue().toString());
                    inicio.setText(snapshot.child("diponibilidad").child("HoraInicio").getValue().toString());
                    fin.setText(snapshot.child("diponibilidad").child("HoraFin").getValue().toString());
                    placa.setText(snapshot.child("diponibilidad").child("placa").getValue().toString());
                    //fecha.setText(snapshot.child("diponibilidad").child("fecha").getValue().toString());
                    ll.setBackgroundColor(Color.GREEN);
                }else{
                    ll.setBackgroundColor(Color.RED);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}