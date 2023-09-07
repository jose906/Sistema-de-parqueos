package com.alcaldia.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class adapter2 extends BaseAdapter {


    LayoutInflater layoutInflater=null;
    Context context;
    int[] monto,nuevo;
    String[]  fecha,hora,viejo;





    public  adapter2 (Context context,int monto[],int[] nuevo, String [] fecha,String [] hora, String[] viejo) {

        this.context=context;
        this.monto=monto;
        this.fecha=fecha;
        this.nuevo=nuevo;
        this.hora=hora;
        this.viejo=viejo;
        layoutInflater =(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return fecha.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View Convertview, ViewGroup viewGroup) {
        View view = layoutInflater.inflate(R.layout.adapter2,null);
        TextView txtmonto=(TextView)view.findViewById(R.id.lista_costo);
        TextView txtnuevo=(TextView)view.findViewById(R.id.lista_Nuevo);
        TextView txtfecha=(TextView)view.findViewById(R.id.lista_Fecha);
        TextView txtviejo=(TextView)view.findViewById(R.id.lista_previo);
        TextView txthora=(TextView)view.findViewById(R.id.lista_hora);


        if(fecha[i]!=null) {
            txtmonto.setText(String.valueOf( monto[i])+" BS");
            txtfecha.setText(fecha[i]);
            txtnuevo.setText(String.valueOf( nuevo[i])+" BS");
            txthora.setText(hora[i]);
            txtviejo.setText(viejo[i]);


        }


        return view;
    }
}
