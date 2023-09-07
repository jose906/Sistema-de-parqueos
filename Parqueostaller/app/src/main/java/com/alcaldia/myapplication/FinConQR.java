package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.lang.ref.Reference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class FinConQR extends AppCompatActivity {
    TextView transcurrido,faltante,costo,fecha;
    ImageView QR;
    String cod,uid,inicio,fin,ampliaciones;
    int cos=0;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    QRGEncoder qrgEncoder;
    Bitmap bitmap;
    Date FechaInicio,FechaFin,actual;
    Spinner ampliar;
    ArrayAdapter<CharSequence> time;
    Button aceptar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_con_qr);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        uid=user.getUid();
        ampliar=(Spinner)findViewById(R.id.ampliar);
        time = ArrayAdapter.createFromResource(this,R.array.horas,R.layout.new_spinner);
        ampliar.setAdapter(time);
        reference=FirebaseDatabase.getInstance().getReference();
        aceptar=(Button)findViewById(R.id.amp);
        transcurrido=(TextView)findViewById(R.id.transcurrido);
        faltante=(TextView)findViewById(R.id.faltante);
        costo=(TextView)findViewById(R.id.costo);
        fecha=(TextView)findViewById(R.id.findate);
        QR=(ImageView)findViewById(R.id.finQr);
        cod=getIntent().getExtras().getString("cod");
        crearqr();

        reference.child("app").child("parqueos").child(cod).child("diponibilidad").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){


                    inicio = dataSnapshot.child("HoraInicio").getValue().toString();
                    fin = dataSnapshot.child("HoraFin").getValue().toString();
                    calculoHoras();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        ampliar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    ampliaciones =  ampliar.getSelectedItem().toString();
                    if (position == 1) {
                        cos = 3;

                    } else {


                        cos = Integer.parseInt(ampliaciones) * 5;

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ampliar.getSelectedItem().toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Calendar calendar=Calendar.getInstance();

                calendar.setTime(FechaFin);
                if(ampliaciones.equals("1/2"))
                {
                    calendar.add(Calendar.MINUTE,30);
                }else {
                    calendar.add(Calendar.HOUR,cos/5);
                }

                Date date = calendar.getTime();

                String currentDateandTime = simpleDateFormat.format(date);
                reference.child("app").child("parqueos").child(cod).child("diponibilidad").child("HoraFin").setValue(currentDateandTime);


            }
        });




        //new CountDownTimer()

    }
    public void calculoHoras(){

        try {

            FechaInicio = new SimpleDateFormat("HH:mm").parse(inicio);
            FechaFin = new SimpleDateFormat("HH:mm").parse(fin);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String currentDateandTime = simpleDateFormat.format(new Date());
            actual = new SimpleDateFormat("HH:mm").parse(currentDateandTime);

           // actual =new SimpleDateFormat("HH:mm");


        }catch (Exception e){

        }

        long dif = FechaFin.getTime() - FechaInicio.getTime();
        Long dif1 = FechaFin.getTime() - actual.getTime();
         final Long dif2 = actual.getTime() - FechaInicio.getTime();
        new CountDownTimer(dif1,1000){

            @Override
            public void onTick(long millisUntilFinished) {

                faltante.setText(String.valueOf(millisUntilFinished/(1000*60))+" min " +(int)((millisUntilFinished / 1000) % 60)+" seg " );

            }

            @Override
            public void onFinish() {

                faltante.setTextColor(Color.RED);
                faltante.setText("su tiempo a finalizado");


            }
        }.start();
        new CountDownTimer(dif2,1000){
            long abc = dif2;

            @Override
            public void onTick(long millisUntilFinished) {

                transcurrido.setText(String.valueOf(abc/(1000*60))+" min " +(int)((abc / 1000) % 60)+" seg " );

                 abc = abc+1000;

            }

            @Override
            public void onFinish() {

                faltante.setTextColor(Color.RED);
                faltante.setText("su tiempo a finalizado");


            }
        }.start();

        //faltante.setText(String.valueOf(dif));
    }
    public void crearqr(){


        if (uid.length() > 0) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
                    uid, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);

            try {

                bitmap = qrgEncoder.encodeAsBitmap();
                QR.setImageBitmap(bitmap);


            } catch (WriterException e) {

            }
        } else {

        }

    }
}
