package com.alcaldia.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        Timer time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {

                if (firebaseUser!=null){

                    Intent intent = new Intent(getApplicationContext(), com.alcaldia.myapplication.MenuPrincipal.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getApplicationContext(), com.alcaldia.myapplication.Login.class);
                    startActivity(intent);
                }

            }
        },3000);








    }
}
