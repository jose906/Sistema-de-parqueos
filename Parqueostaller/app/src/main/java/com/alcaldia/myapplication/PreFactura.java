package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class PreFactura extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    String llave,uid,monto1,razon,ci;
    TextView monto;
    EditText fac,nit;
    Button facturar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_factura);

        llave=getIntent().getExtras().getString("key");
        monto = (TextView)findViewById(R.id.FacturaMonto);
        facturar=(Button)findViewById(R.id.generar);

        fac = (EditText)findViewById(R.id.razonSocial);
        nit = (EditText)findViewById(R.id.nit);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();
        reference= FirebaseDatabase.getInstance().getReference();
        reference.child("app").child("usuarios").child(uid).child("recargas").child(llave).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    monto1 = dataSnapshot.child("Carga").getValue().toString();
                    monto.setText(dataSnapshot.child("Carga").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        facturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                razon = fac.getText().toString();
                ci = nit.getText().toString();
                Intent intent = new Intent(getApplicationContext(),com.alcaldia.myapplication.Factura.class );
                intent.putExtra("nombre",razon);
                intent.putExtra("nit",ci);
                intent.putExtra("monto",monto1);
                intent.putExtra("cod",llave);
                startActivity(intent);
            }
        });


    }

    public void createPDF(){

         PdfDocument pdfDocument = new PdfDocument();
         Paint paint = new Paint();
         PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400,600,1).create();
         PdfDocument.Page page = pdfDocument.startPage(pageInfo);
         Canvas canvas = page.getCanvas();
         canvas.drawText("Factura de consumo",40,50,paint);
         paint.setTextAlign(Paint.Align.CENTER);
         paint.setTextSize(12f);
         canvas.drawText("Factura",(pageInfo.getPageWidth()/2),30,paint);
         paint.setColor(Color.BLUE);
         canvas.drawText("callesn",(pageInfo.getPageWidth()/2),40,paint);
         paint.setTextAlign(Paint.Align.LEFT);
         paint.setTextSize(8.0f);
         pdfDocument.finishPage(page);
         pdfDocument.close();
         //File file =new File(Environment.getExternalStorageDirectory(),"/Hello.pdf");


    }
}
