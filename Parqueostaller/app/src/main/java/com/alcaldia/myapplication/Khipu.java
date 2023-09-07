package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Khipu extends AppCompatActivity {

    WebView webView;
    String url, uid;
    Button aceptar;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference  databaseReference;
    int saldos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khipu);
        aceptar=(Button)findViewById(R.id.cargaex);
        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        uid=user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        url = getIntent().getExtras().getString("url");
        databaseReference.child("app").child("usuarios").child(uid).child("saldo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String saldo = dataSnapshot.getValue().toString();
                     saldos = Integer.parseInt(saldo);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        webView=(WebView)findViewById(R.id.web);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.measure(10,10);
        webView.loadUrl(url);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadWithOverviewMode(true);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url2 = webView.getUrl();
                if(url2.contains("errorMessage=Successful")){

                    int a = url2.indexOf("Amount=");
                    int b = url2.indexOf("&purchaseVerification");

                    String monto=url2.substring(a,b);
                    int c= monto.indexOf("=");
                    c=c+1;
                    int d = monto.length();
                    d=d-2;
                    monto = monto.substring(c,d);
                    int montos = Integer.parseInt(monto);
                    saldos=saldos+montos;
                    String sa= String.valueOf(saldos);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    String currentDateandTime = simpleDateFormat.format(new Date());
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDateandTime1 = simpleDateFormat1.format(new Date());
                    Map<String,Object> map=new HashMap<>();
                    map.put("Carga",montos);
                    map.put("SaldoAntiguo",saldos-montos);
                    map.put("NuevoSaldo",saldos);
                    map.put("Fecha",currentDateandTime1);
                    map.put("Hora",currentDateandTime);
                    //map.put("HoraInicio",currentDateandTime1);
                    databaseReference.child("app").child("usuarios").child(uid).child("saldo").setValue(sa);
                    databaseReference.child("app").child("usuarios").child(uid).child("recargas").push().setValue(map);

                    AlertDialog.Builder alert=new AlertDialog.Builder(Khipu.this);
                    alert.setMessage("Su carga fue exitosa").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                            Intent intent = new Intent(getApplicationContext(),com.alcaldia.myapplication.Saldo.class);
                            startActivity(intent);


                        }
                    });
                    AlertDialog dialog=alert.create();
                    dialog.setTitle("Exito");
                    dialog.show();

                    //databaseReference.child("app").child("usuarios").child(uid).child("saldo").setValue(String.valueOf(a));



                }else {

                    AlertDialog.Builder alert=new AlertDialog.Builder(Khipu.this);
                    alert.setMessage("esta seguro que desea salir sin finalizar").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {




                        }
                    });
                    AlertDialog dialog=alert.create();
                    dialog.setTitle("Alerta");
                    dialog.show();
                }
            }
        });


    }
}
