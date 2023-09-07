package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistorialCargas extends AppCompatActivity {

    ListView listView;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    int conta=0,a=0;
    String uid;
    String [] fecha,key;
    int [] monto,nuevo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_cargas);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();
        reference=FirebaseDatabase.getInstance().getReference();
        listView=(ListView) findViewById(R.id.lista);
        cargarlista();

    }
    public void cargarlista(){





        reference.child("app").child("usuarios").child(uid).child("recargas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    for(DataSnapshot ds1 : dataSnapshot.getChildren()){

                     conta++;

                    }
                    fecha = new String[conta];
                    key = new String[conta];
                    nuevo=new int[conta];
                    monto=new int[conta];
                    for (DataSnapshot ds1 : dataSnapshot.getChildren()){

                       key[a] = ds1.getKey();
                       monto[a] = Integer.parseInt(ds1.child("Carga").getValue().toString());
                       nuevo[a] = Integer.parseInt( ds1.child("NuevoSaldo").getValue().toString());
                       fecha[a] = ds1.child("Fecha").getValue().toString();
                       a++;


                    }
                    //Toast.makeText(HistorialCargas.this,String.valueOf(nuevo[1]), Toast.LENGTH_SHORT).show();

                    if(fecha!=null){

                        listView.setAdapter(new adapter(getApplicationContext(),monto,nuevo,fecha));
                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), com.alcaldia.myapplication.PreFactura.class);
                intent.putExtra("key",key[position]);
                startActivity(intent);




            }
        });



    }
}
