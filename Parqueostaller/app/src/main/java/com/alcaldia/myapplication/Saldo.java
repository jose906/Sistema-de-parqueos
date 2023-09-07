package com.alcaldia.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Saldo extends AppCompatActivity {

    Spinner montos;
    ArrayAdapter<CharSequence> arrayAdapter;
    Button sistema,cargas;
    EditText carga;
    TextView saldo;
    FirebaseUser user;
    FirebaseAuth auth;
    String saldos;
    int id=0,saldoint;
    String uid,clientID="AaTwBM-z-9JEp82LGZZBjK8pXr1Itj73dHYftbSxWd6mzZgmo4zzQFjz9TlYFSAZLsilVJHhGZSgVD1D";
    String clientID2="AY10rDuVNU52As7p2IpzWAM4c5S0_FJv0ohrrEPFhsycHC7IU3_6c7Ub-8UlDv97Wxo2F3DaSkHydFQT";

    DatabaseReference databaseReference;
    private PaymentsClient paymentsClient;
    public static final int paypalcode=1111;
    private PayPalConfiguration payPalConfiguration =
            new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(clientID);

    private static JSONObject baseConfigurationObject() throws Exception {


        return new JSONObject().put("apiVersion",2).put("apiVersionMinor",0).put("AllowedPaymentMethods",new JSONArray().put("DISCOVER")
                .put("INTERAC")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA"));


    }

    @Override
    protected void onDestroy() {

        stopService(new Intent(this,PayPalService.class));

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo);
        saldo=(TextView)findViewById(R.id.saldo);
        //montos=(Spinner)findViewById(R.id.monto);
        cargas=(Button)findViewById(R.id.btncargas);
       // arrayAdapter=ArrayAdapter.createFromResource(this,R.array.montos,android.R.layout.simple_spinner_item);
       // montos.setAdapter(arrayAdapter);
        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        uid = user.getUid();
        carga = (EditText)findViewById(R.id.edit_carga);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("app").child("usuarios").child(uid).child("saldo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    saldos = dataSnapshot.getValue().toString();
                     saldoint = Integer.parseInt(saldos);
                    saldo.setText(saldos);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
        paymentsClient = Wallet.getPaymentsClient(this, walletOptions);


        sistema=(Button)findViewById(R.id.sistema);
        sistema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(carga.getText().toString().length()>1){

                    String a = carga.getText().toString();
                    pagos(a);

                }else {

                    Toast.makeText(Saldo.this, "Elija un monto", Toast.LENGTH_SHORT).show();

                }

            }
        });


          /*  sistema.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getApplicationContext(), com.alcaldia.myapplication.Khipu.class);
                        intent.putExtra("url", "https://kh.cm/0Crcw");
                        startActivity(intent);
                        finish();


                    }
                });*/
            cargas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), com.alcaldia.myapplication.HistorialCargas.class);
                    startActivity(intent);
                }
            });
            Intent intent = new Intent(this,PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
            startService(intent);

    }

    public  void hola(){
         try {
             final  JSONObject payrequest = baseConfigurationObject();
             payrequest.put("transactionInfo",new JSONObject().put("tatalPrice",100).put("totalPriceStatus","FINAL").put("currencyCode","USD"));
             payrequest.put("merchantInfo", new JSONObject().put("merchantId","1234567876543").put("merchantName","juanes"));
             final PaymentDataRequest paymentDataRequest = PaymentDataRequest.fromJson(payrequest.toString());
             AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(paymentDataRequest),this, 901);
             //Log.e("h","h");
         }catch (Exception e){



         }


    }

    public void pagos(String a){

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(a),"USD","Carga credito",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,paypalcode);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

            if (requestCode == paypalcode){
                if (resultCode == RESULT_OK){

                    PaymentConfirmation paymentConfirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if(paymentConfirmation!=null){

                        try {

                            String paydetails = paymentConfirmation.toJSONObject().toString(4);
                            Log.e("Todo bien",paydetails);
                            AlertDialog.Builder alert=new AlertDialog.Builder(this);
                            alert.setMessage("Su carga fue exitosa").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String a = carga.getText().toString();
                                    int b = Integer.parseInt(a);
                                    saldoint = saldoint + b;
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                    String currentDateandTime = simpleDateFormat.format(new Date());
                                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                                    String currentDateandTime1 = simpleDateFormat1.format(new Date());
                                    Map<String,Object> map=new HashMap<>();
                                    map.put("Carga", b);
                                    map.put("SaldoAntiguo",saldoint-b);
                                    map.put("NuevoSaldo",saldoint);
                                    map.put("Fecha",currentDateandTime1);
                                    map.put("Hora",currentDateandTime);
                                    //map.put("HoraInicio",currentDateandTime1);
                                    databaseReference.child("app").child("usuarios").child(uid).child("saldo").setValue(saldoint);
                                    databaseReference.child("app").child("usuarios").child(uid).child("recargas").push().setValue(map);
                                    saldo.setText("");




                                }
                            });
                            AlertDialog dialog=alert.create();
                            dialog.setTitle("Exito");
                            dialog.show();
                            //startActivity(new Intent(this, PayPalPaymentDetails.class));


                        }catch (Exception e){

                        }
                    }


                }

            }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
