package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class InicioConQR extends AppCompatActivity {

    TextView zona, inicio,fin, calle, numero,fecha;
    String cod,uid;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_con_qr);
        zona=(TextView)findViewById(R.id.zone);
        inicio=(TextView)findViewById(R.id.horaInicio);
        fin=(TextView)findViewById(R.id.horaFin);
        calle=(TextView)findViewById(R.id.street);
        numero=(TextView)findViewById(R.id.num);
        fecha=(TextView)findViewById(R.id.date);
        imageView=(ImageView)findViewById(R.id.codigoQr);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        uid=user.getUid();
        reference= FirebaseDatabase.getInstance().getReference();
        cod=getIntent().getExtras().getString("cod");
        reference.child("app").child("parqueos").child(cod).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                calle.setText(dataSnapshot.child("calle").getValue().toString());
                zona.setText(dataSnapshot.child("zona").getValue().toString());
                numero.setText(dataSnapshot.child("numero").getValue().toString());
                fin.setText(dataSnapshot.child("diponibilidad").child("HoraFin").getValue().toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String currentDateandTime = simpleDateFormat.format(new Date());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime1 = simpleDateFormat1.format(new Date());
        inicio.setText(currentDateandTime);
        fecha.setText(currentDateandTime1);

        crearqr();
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
                imageView.setImageBitmap(bitmap);


            } catch (WriterException e) {

            }
        } else {

        }
    }
}
