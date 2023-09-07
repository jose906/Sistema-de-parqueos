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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    Button regis;
    EditText carnet, nombre,apellido,correo,contra;
    FirebaseAuth auth;
    String nombre1,carnet1,apellido1,correo1,contra1;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        carnet=(EditText)findViewById(R.id.carnet);
        nombre=(EditText)findViewById(R.id.nombre);
        apellido=(EditText)findViewById(R.id.apellido);
        correo=(EditText)findViewById(R.id.mail);
        contra=(EditText)findViewById(R.id.pass);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        regis=(Button)findViewById(R.id.registrar);
        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                auth.createUserWithEmailAndPassword(correo1,contra1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            String UserId = user.getUid();
                            nombre1=nombre.getText().toString();
                            apellido1=apellido.getText().toString();
                            correo1=correo.getText().toString();
                            carnet1=carnet.getText().toString();
                            contra1=contra.getText().toString();

                            Map<String,Object> map=new HashMap<>();
                            map.put("nombre",nombre1);
                            map.put("apellido",apellido1);
                            map.put("carnet",carnet1);
                            map.put("correo",correo1);
                            map.put("contra",contra1);
                            map.put("saldo", "0");
                            map.put("reserva","no");


                            databaseReference.child("app").child("usuarios").child(UserId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        AlertDialog.Builder alert=new AlertDialog.Builder(Registro.this);
                                        alert.setMessage("su registro fue exitoso").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent=new Intent(getApplicationContext(),com.alcaldia.myapplication.MainActivity.class);
                                                startActivity(intent);


                                            }
                                        });
                                        AlertDialog dialog=alert.create();
                                        dialog.setTitle("Exito");
                                        dialog.show();


                                    }else {

                                        Toast.makeText(Registro.this, "Algo fallo", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });



                        }else {


                        }


                    }
                });
            }
        });
    }
}
