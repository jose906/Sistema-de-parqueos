package com.alcaldia.contolador_taller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Reserva_manual extends AppCompatActivity {

    DatabaseReference databaseReference;
    ArrayAdapter<String> arrayAdapter,arrayAdapter1,arrayAdapter2;
    Spinner spinner,spinner2,spinner3;
    List<String> nomeConsulta = new ArrayList<String>();
    List<String> calles = new ArrayList<String>();
    List<String> numero = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_manual);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.new_spinner, nomeConsulta);
        arrayAdapter1 = new ArrayAdapter<String>(this,R.layout.new_spinner,calles);
        spinner2 = (Spinner)findViewById(R.id.manual_calle);
        spinner = (Spinner)findViewById(R.id.manual_zona);
        lista_zonas();


         spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 lista_calles(spinner.getSelectedItem().toString());

             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }


         });


    }

    public void lista_zonas(){

        databaseReference.child("app").child("parqueos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String a = ds.child("zona").getValue().toString();
                        if(nomeConsulta.contains(a)){

                        }else {

                            nomeConsulta.add(a);
                        }

                    }
                }
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void lista_calles (final String zona){


        databaseReference.child("app").child("parqueos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String a = ds.child("calle").getValue().toString();
                        if(calles.contains(a)){

                        }else {
                            if(ds.child("zona").getValue().toString().equals(zona)) {
                                calles.add(a);
                            }
                        }

                    }
                }
                spinner2.setAdapter(arrayAdapter1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        calles.clear();


    }
    public void lista_numero (final String calle){


        databaseReference.child("app").child("parqueos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String a = ds.child("numero").getValue().toString();
                        if(numero.contains(a)){

                        }else {
                            if(ds.child("zona").getValue().toString().equals(calle)) {
                                numero.add(a);
                            }
                        }

                    }
                }
                spinner3.setAdapter(arrayAdapter2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        numero.clear();


    }
}