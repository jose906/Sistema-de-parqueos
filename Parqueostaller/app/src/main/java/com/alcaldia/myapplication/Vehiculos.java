package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Vehiculos extends AppCompatActivity {

    EditText placa,modelo,marca,year,color;
    Button registro;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String pl,mo,ma,ye;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos);

        placa=(EditText)findViewById(R.id.placa);
        modelo=(EditText)findViewById(R.id.modelo);
        marca=(EditText)findViewById(R.id.marca);
        year=(EditText)findViewById(R.id.year);
        //color=(EditText)findViewById(R.id.placa);
        registro=(Button)findViewById(R.id.autosregistr);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pl=placa.getText().toString();
                mo=modelo.getText().toString();
                ma=marca.getText().toString();
                ye=year.getText().toString();
                Map<String,Object> map=new HashMap<>();
                map.put("placa",pl);
                map.put("modelo",mo);
                map.put("marca",ma);
                map.put("year",ye);

                databaseReference.child("app").child("usuarios").child(user.getUid()).child("autos").child(pl).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if(task.isSuccessful()){

                            AlertDialog.Builder alert=new AlertDialog.Builder(Vehiculos.this);
                            alert.setMessage("su registro fue exitoso").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent=new Intent(getApplicationContext(),com.alcaldia.myapplication.MenuPrincipal.class);
                                    startActivity(intent);


                                }
                            });
                            AlertDialog dialog=alert.create();
                            dialog.setTitle("Exito");
                            dialog.show();


                        }else {

                        }

                    }
                });



            }
        });



    }
}
