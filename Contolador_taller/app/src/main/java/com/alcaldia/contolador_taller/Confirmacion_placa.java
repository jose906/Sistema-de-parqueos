package com.alcaldia.contolador_taller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Confirmacion_placa extends AppCompatActivity {

    Button aceptar, iniciar, ver;
    EditText placa;
    TextView llegada, reserva;
    String pla, id;
    DatabaseReference reference;
    Boolean estado = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_placa);

        reference = FirebaseDatabase.getInstance().getReference();
        reserva = (TextView) findViewById(R.id.reserva);
        llegada = (TextView) findViewById(R.id.star);

        placa = (EditText) findViewById(R.id.numeplaca);
        aceptar = (Button) findViewById(R.id.aceptar);
        iniciar = (Button) findViewById(R.id.iniciar);
        ver = (Button) findViewById(R.id.ver);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pla = placa.getText().toString();


                reference.child("app").child("parqueos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (ds.child("diponibilidad").child("disponibilidad").getValue().toString().equals("ocupado") && ds.child("diponibilidad").child("placa").getValue().toString().equals(pla)) {

                                    id = ds.getKey().toString();

                                    reserva.setText("El usuario tiene una reserva valida");
                                    reserva.setTextColor(Color.GREEN);
                                    if (ds.child("diponibilidad").child("llegada").getValue().toString().equals("si")) {

                                        llegada.setText("El usuario inicio hora");
                                        llegada.setTextColor(Color.GREEN);
                                        ver.setEnabled(true);

                                    } else {

                                        llegada.setText("El usuario todavia no inicia hora");
                                        llegada.setTextColor(Color.RED);
                                        iniciar.setEnabled(true);

                                    }
                                    estado = true;
                                } else {

                                    if (estado) {

                                    } else {

                                        reserva.setText("El usuario no tiene una reserva valida");
                                        reserva.setTextColor(Color.RED);
                                    }

                                }


                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("app").child("parqueos").child(id).child("diponibilidad").child("llegada").setValue("si");
            }
        });
        ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), com.alcaldia.contolador_taller.Confirmacion.class);
                intent.putExtra("uid",id);
                startActivity(intent);
            }
        });

    }
}
