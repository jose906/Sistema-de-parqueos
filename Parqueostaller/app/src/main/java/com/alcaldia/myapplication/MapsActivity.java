package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    double lat=0.0,lon=0.0;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    Double lat1=0.0,lon1=0.0;
    String reserva="hola",uid,llegada="no",solicitar="no";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        reference= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        uid=user.getUid();

        if(getIntent().getStringExtra("nuevo") != null){

            solicitar = getIntent().getStringExtra("nuevo");
            cargarmapa();

        }
        else {

            reference.child("app").child("usuarios").child(uid).child("reserva").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        reserva = dataSnapshot.getValue().toString();
                        if (reserva.equals("no")) {
                            cargarmapa();
                        } else {

                                setReserva(reserva);
                                fin(reserva);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setLocationManager();

        //parqueo();



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker markers) {
               final String txt = markers.getTitle().toString();
                LatLng lng = markers.getPosition();
                double la = lng.latitude, lo = lng.longitude;


                if(txt.contains("mi ubicacion")){

                    Toast.makeText(MapsActivity.this, "Mi Ubicacion", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(solicitar.equals("no")){

                        if (reserva.equals("no")) {
                            Intent intent = new Intent(getApplicationContext(), com.alcaldia.myapplication.Reserva.class);
                            intent.putExtra("lat", la);
                            intent.putExtra("lon", lo);
                            intent.putExtra("cod", txt);

                            startActivity(intent);
                        } else {
                            if (llegada.equals("no")) {
                                Intent intent = new Intent(getApplicationContext(), com.alcaldia.myapplication.InicioConQR.class);
                                intent.putExtra("cod", txt);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), com.alcaldia.myapplication.FinConQR.class);
                                intent.putExtra("cod", txt);
                                startActivity(intent);

                            }

                        }

                    }else {

                        reference.child("app").child("parqueos").child(solicitar).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String currentDateandTime = dataSnapshot.child("diponibilidad").child("HoraFin").getValue().toString();
                                String currentDateandTime1 = dataSnapshot.child("diponibilidad").child("HoraInicio").getValue().toString();
                                String placa = dataSnapshot.child("diponibilidad").child("placa").getValue().toString();
                                Map<String,Object> map=new HashMap<>();
                                map.put("disponibilidad","disponible");
                                map.put("usuario","no");
                                map.put("placa","no");
                                map.put("llegada","no");
                                map.put("HoraFin","00:00");
                                map.put("HoraInicio","00:00");
                                Map<String,Object> map1=new HashMap<>();
                                map1.put("disponibilidad","ocupado");
                                map1.put("usuario",uid);
                                map1.put("placa",placa);
                                map1.put("llegada","no");
                                map1.put("HoraFin",currentDateandTime);
                                map1.put("HoraInicio",currentDateandTime1);
                                reference.child("app").child("parqueos").child(solicitar).child("diponibilidad").setValue(map);
                                reference.child("app").child("parqueos").child(txt).child("diponibilidad").setValue(map1);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                }
                return false;
            }
        });
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private void Setmarker (double lat, double lon){

        LatLng  coordenadas = new LatLng(lat,lon);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas,16);
        if (marker!= null){
            marker.remove();
        }

        marker=mMap.addMarker(new MarkerOptions().position(coordenadas).title("mi ubicacion").icon(BitmapDescriptorFactory.defaultMarker()));
        mMap.animateCamera(miUbicacion);

    }


    private void parqueo(){
        LatLng coordenadas = new LatLng(-16.545238,-68.092643);
        //mMap.addddPolyline(new PolylineOptions().add(coordenadas));
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(coordenadas)
                .radius(10)
                .strokeColor(Color.RED)
                .fillColor(Color.RED));
        circle.setClickable(true);


    }

    private void setLocation(Location location1){
        if(location1!=null){

            lon = location1.getLongitude();
            lat = location1.getLatitude();
            Setmarker(lat,lon);


        }


    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            setLocation(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void setLocationManager(){

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
        return;
    }
    LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    setLocation(location);
   // Toast.makeText(this, String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,locationListener);

    }
    public  void cargarmapa(){

        reference.child("app").child("parqueos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        if (ds.child("diponibilidad").child("disponibilidad").getValue().toString().equals("disponible")) {


                            lat1 = Double.parseDouble(ds.child("latitude").getValue().toString());
                            lon1 = Double.parseDouble(ds.child("longitud").getValue().toString());
                            Log.e("lat",String.valueOf(lat1));
                            LatLng sydney = new LatLng(lat1, lon1);
                            Marker marker1 = mMap.addMarker(new MarkerOptions().position(sydney).
                                    title(ds.getKey().toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador_de_posicion_3)));
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public  void setReserva(String cod){

        reference.child("app").child("parqueos").child(cod).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                        lat1=Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                        lon1=Double.parseDouble(dataSnapshot.child("longitud").getValue().toString());

                        LatLng sydney = new LatLng(lat1,lon1);
                        Marker marker1 = mMap.addMarker(new MarkerOptions().position(sydney).title(dataSnapshot.getKey().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void fin(String txt){

        reference.child("app").child("parqueos").child(txt).child("diponibilidad").child("llegada").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    llegada=dataSnapshot.getValue().toString();
                    Toast.makeText(MapsActivity.this, llegada, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MapsActivity.this, "quete asbda", Toast.LENGTH_SHORT).show();
                }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}



/**
 * Manipulates the map once available.
 * This callback is triggered when the map is ready to be used.
 * This is where we can add markers or lines, add listeners or move the camera. In this case,
 * we just add a marker near Sydney, Australia.
 * If Google Play services is not installed on the device, the user will be prompted to install
 * it inside the SupportMapFragment. This method will only be triggered once the user has
 * installed Google Play services and returned to the app.
 */
