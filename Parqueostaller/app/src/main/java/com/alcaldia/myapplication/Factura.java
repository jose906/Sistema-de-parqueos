package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.DocumentException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Factura extends AppCompatActivity {

    int view=R.layout.activity_main;
    ImageView screenShot;
    QRGEncoder qrgEncoder;
    TextView textView;
    Bitmap bitmap;
    LinearLayout layout;
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "factura.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";
    FirebaseAuth auth;
    DatabaseReference reference;
    String social, nit,monto,uid,llave, fecha, hora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);
        layout=findViewById(R.id.facturar);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1000);
        } else {
        }

        reference = FirebaseDatabase.getInstance().getReference();


        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        llave = getIntent().getStringExtra("cod");
        social = getIntent().getStringExtra("nombre");
        nit = getIntent().getStringExtra("nit");
        monto = getIntent().getStringExtra("monto");
        getdata();
        AlertDialog.Builder alert=new AlertDialog.Builder(Factura.this);
        alert.setMessage("Su factura fue generada exitosamente").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent=new Intent(getApplicationContext(),com.alcaldia.myapplication.HistorialCargas.class);
                startActivity(intent);


            }
        });
        AlertDialog dialog=alert.create();
        dialog.setTitle("Exito");
        dialog.show();






    }


    public static Bitmap loadBitmapfromview(View v,int width, int heigt){


        Bitmap btm = Bitmap.createBitmap(400,600,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(btm);
        v.draw(canvas);
        return btm;

    }
    public void getdata(){


        reference.child("app").child("usuarios").child(uid).child("recargas").child(llave).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               fecha = dataSnapshot.child("Fecha").getValue().toString();
               hora = dataSnapshot.child("Hora").getValue().toString();
                pdf();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void pdf() {



        // Se crea el documento
        Document documento = new Document(PageSize.A3);
        Paragraph t = new Paragraph("Factura");


        try {
            File f = crearFichero(NOMBRE_DOCUMENTO);
            // Se crea el OutputStream para el fichero donde queremos dejar el pdf.

            FileOutputStream ficheroPdf = new FileOutputStream(f.getAbsolutePath());
            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);

            // Abrimos el documento.
            documento.open();

            // Añadimos un titulo con la fuente por defecto.
            t.setAlignment(Element.ALIGN_CENTER);
            documento.add(t);



            //Font font = FontFactory.getFont(FontFactory.HELVETICA, 28, com.itextpdf.text.Font.BOLD, Color.RED);
            Font font = FontFactory.getFont(FontFactory.HELVETICA,29);


            documento.add(new Paragraph("Factura no: 123456789"));
            documento.add(new Paragraph("Autorizacion no: 38740100011457"));
            documento.add(new Paragraph("GOBIERNO AUTONOMO"));
            documento.add(new Paragraph("MUNICIPAL DE LA PAZ"));
            documento.add(new Paragraph("Fecha:" + fecha + "-"+hora));
            documento.add(new Paragraph("Nombre: "+ social));
            documento.add(new Paragraph("NIT/CI: "+ nit));





            // Insertamos una imagen que se encuentra en los recursos de la
            // aplicacion.


            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(4);
            tabla.addCell("CANT");
            tabla.addCell("CONCEPTO");
            tabla.addCell("PRECIO");
            tabla.addCell("SUBTOTAL");
            tabla.addCell("1");
            tabla.addCell("EST-PUB");
            tabla.addCell(monto);
            tabla.addCell(monto);
            documento.add(tabla);
            documento.add(new Paragraph("Total: "+monto));
            documento.add(new Paragraph("Codigo de control: F2-46-63-9A"));
            documento.add(new Paragraph("Fecha limite de emision: 05/05/2021"));

            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 1 / 2;

                qrgEncoder = new QRGEncoder("123456789 38740100011457 F2-46-63-9A 05/05/2021", null,
                        QRGContents.Type.TEXT,
                        smallerDimension);

                try {

                    bitmap = qrgEncoder.encodeAsBitmap();
                    //imageView.setImageBitmap(bitmap);


                } catch (WriterException e) {

                }

            //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ano_nuevo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            documento.add(imagen);

            // Agregar marca de agua
           // font = FontFactory.getFont(FontFactory.HELVETICA, 42, Color.GRAY);
            //ColumnText.showTextAligned(writer.getDirectContentUnder(), Element.ALIGN_CENTER, new Paragraph("androfast.com", font), 297.5f, 421, writer.getPageNumber() % 2 == 1 ? 45 : -45);



        }catch (DocumentException e) {

            Log.e(ETIQUETA_ERROR, e.getMessage());

        } catch (IOException e) {

            Log.e(ETIQUETA_ERROR, e.getMessage());

        } finally {
            // Cerramos el documento.
            documento.close();
        }



    }
    public static File crearFichero(String nombreFichero) throws IOException {
        File ruta = getRuta();
        File fichero = null;
        if (ruta != null)
            fichero = new File(ruta, nombreFichero);
        return fichero;
    }
    public static File getRuta() {

        // El fichero sera almacenado en un directorio dentro del directorio
        // Descargas
        File ruta = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            ruta = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), NOMBRE_DIRECTORIO);

            if (ruta != null) {
                if (!ruta.mkdirs()) {
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }
        } else {
            Log.e(ETIQUETA_ERROR, "null");
        }

        return ruta;
    }



}
