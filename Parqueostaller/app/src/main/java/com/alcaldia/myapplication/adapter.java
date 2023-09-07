package com.alcaldia.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class adapter extends BaseAdapter {

    LayoutInflater layoutInflater=null;
    Context context;
    int[] monto,nuevo;
    String[]  fecha;



    public  adapter (Context context,int monto[],int[] nuevo, String [] fecha) {

        this.context=context;
        this.monto=monto;
        this.fecha=fecha;
        this.nuevo=nuevo;

        layoutInflater =(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return fecha.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = layoutInflater.inflate(R.layout.adapter,null);
        TextView txtmonto=(TextView)view.findViewById(R.id.VerCarga);
        TextView txtnuevo=(TextView)view.findViewById(R.id.VerNuevo);
        TextView txtfecha=(TextView)view.findViewById(R.id.VerFecha);

        if(fecha[position]!=null) {
            txtmonto.setText(String.valueOf( monto[position])+" BS");
            txtfecha.setText(fecha[position]);
            txtnuevo.setText(String.valueOf( nuevo[position])+" BS");


        }


        return view;
    }
}
