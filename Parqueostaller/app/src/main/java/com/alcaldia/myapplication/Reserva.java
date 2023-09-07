package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Trace;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Reserva extends AppCompatActivity {

    TextView calle, numero, zona,fecha,hora;
    LatLng latLng;
    int cos;
    String cod,uid,costo,placa,tiempo;
    double lat=0.0,lon=0.0;
    DatabaseReference reference;
    Button ver,reservar;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Spinner spinner, horas;
    List<String> nomeConsulta = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<CharSequence> time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);
        lat=getIntent().getExtras().getDouble("lat");
        lon=getIntent().getExtras().getDouble("lon");
        cod=getIntent().getExtras().getString("cod");
        calle=(TextView)findViewById(R.id.calle);
        numero=(TextView)findViewById(R.id.numero);
        zona=(TextView)findViewById(R.id.zona);
        fecha=(TextView)findViewById(R.id.fecha);
        hora=(TextView)findViewById(R.id.hora);
        spinner=(Spinner)findViewById(R.id.car);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.new_spinner, nomeConsulta);
        time = ArrayAdapter.createFromResource(this,R.array.horas,R.layout.new_spinner);
         horas=(Spinner)findViewById(R.id.time);
         horas.setAdapter(time);
        auth=FirebaseAuth.getInstance();
        firebaseUser= auth.getCurrentUser();
        uid=firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        reservar=(Button)findViewById(R.id.reservar);
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

        horas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0) {
                    costo = horas.getSelectedItem().toString();
                    if (position == 1) {
                        cos = 3;

                    } else {


                        cos = Integer.parseInt(costo) * 5;
                        Toast.makeText(Reserva.this, String.valueOf(cos), Toast.LENGTH_SHORT).show();
                    }
                }else{

                    Toast.makeText(Reserva.this, "seleccione su tiempo", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String currentDateandTime = simpleDateFormat.format(new Date());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        final String currentDateandTime1 = simpleDateFormat1.format(new Date());
        hora.setText(currentDateandTime);
        fecha.setText(currentDateandTime1);
        //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        reference= FirebaseDatabase.getInstance().getReference();
        reference.child("app").child("parqueos").child(cod).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                calle.setText(dataSnapshot.child("calle").getValue().toString());
                zona.setText(dataSnapshot.child("zona").getValue().toString());
                numero.setText(dataSnapshot.child("numero").getValue().toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                placa = spinner.getSelectedItem().toString();

                Intent intent = new Intent(getApplicationContext(), com.alcaldia.myapplication.Confirmar_reserva.class);
                intent.putExtra("costo",cos);
                intent.putExtra("placa",placa);
                intent.putExtra("lat",lat);
                intent.putExtra("lon",lon);
                intent.putExtra("cod",cod);
                intent.putExtra("tiempo",costo);
                intent.putExtra("fecha",currentDateandTime1);
                startActivity(intent);
                finish();




            }
        });




    }
}
