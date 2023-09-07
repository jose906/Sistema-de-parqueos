package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportarProblema extends AppCompatActivity {

    Button solicitar;
    DatabaseReference reference;
    String uid,id;
    FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportar_problema);
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("app").child("usuarios").child(uid).child("reserva").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()) {
                    id = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        solicitar = (Button)findViewById(R.id.btn_solicitar);
        solicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),com.alcaldia.myapplication.MapsActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
    }
}