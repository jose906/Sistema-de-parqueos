package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Perfil extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseAuth auth;
    String uid;
    ArrayAdapter<String> arrayAdapter;
    Spinner spinner;
    List<String> nomeConsulta = new ArrayList<String>();
    Button eliminar;
    TextView nombre,licencia,correo,cerrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.new_spinner, nomeConsulta);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        spinner = (Spinner)findViewById(R.id.misautos);
        eliminar = (Button)findViewById(R.id.deletecar);
        nombre = (TextView)findViewById(R.id.name);
        licencia = (TextView)findViewById(R.id.licencia);
        correo = (TextView)findViewById(R.id.correo);
        cerrar = (TextView)findViewById(R.id.cerrar_sesion);
        datos();
        listasautos();
        eliminarcar();
        cerrar_sesion();
    }
    public void cerrar_sesion(){

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 auth.signOut();
                 Intent intent = new Intent(getApplicationContext(), com.alcaldia.myapplication.Login.class);
                 startActivity(intent);
                 finish();
            }
        });

    }

    public void datos(){

        databaseReference.child("app").child("usuarios").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                   String name = dataSnapshot.child("nombre").getValue().toString() + " "+dataSnapshot.child("apellido").getValue().toString();
                    nombre.setText(name);
                    licencia.setText(dataSnapshot.child("carnet").getValue().toString());
                    correo.setText(dataSnapshot.child("correo").getValue().toString());


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void listasautos(){

        databaseReference.child("app").child("usuarios").child(uid).child("autos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    nomeConsulta.add("seleccione su vehiculo");

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String a = ds.child("placa").getValue().toString();
                        nomeConsulta.add(a);

                    }
                }
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void eliminarcar(){

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              final String a = spinner.getSelectedItem().toString();

                AlertDialog.Builder alert=new AlertDialog.Builder(Perfil.this);
                alert.setMessage("Esta seguro de que desea eliminar ese vehiculo").setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        databaseReference.child("app").child("usuarios").child(uid).child("autos").child(a).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                nomeConsulta.clear();

                            }
                        });


                    }
                }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog=alert.create();
                dialog.setTitle("Eliminar");
                dialog.show();




            }
        });

    }



}
