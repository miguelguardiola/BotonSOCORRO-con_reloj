package com.es.upv.etsit.aatt.botonsocorro;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class WearMainActivity extends WearableActivity {

    private static final String WEAR_ARRANCAR_ACTIVIDAD= "/arrancar_actividad";
    private GoogleApiClient apiClient;



    ///////////////////////////////METODO ON CREATE PARA INICIALIZAR ///////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_main);
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        /////////////////////////// CONFIGURACIÓN DEL BOTON  ///////////////////////////////////////

        Button botonLanzar = (Button) findViewById(R.id.botonSOS);
        botonLanzar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mandarMensaje(WEAR_ARRANCAR_ACTIVIDAD, "");
                //Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
            }
        });


    }

    ///////////////////////////////METODO ON START PARA INICIAR LA CONEXION ////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }
///////////////////////////////METODO ON STOP PARA PARAR LA CONEXION ////////////////////////
    @Override
    protected void  onStop() {
        if (apiClient != null && apiClient.isConnected()) {
            apiClient.disconnect();
            Toast.makeText(getApplicationContext(), "Desconectado", Toast.LENGTH_LONG).show();
        }
        super.onStop();
    }

/////////////////////////// METODO mandarMensaje PARA ENVIAR MENSAJE ///////////////////////////////

    private void mandarMensaje(final String path, final String texto) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                NodeApi.GetConnectedNodesResult nodos =
                        Wearable.NodeApi.getConnectedNodes(apiClient).await();
                for (Node nodo : nodos.getNodes()) {
                    Wearable.MessageApi.sendMessage(apiClient, nodo.getId(), path, texto.getBytes())
                            .setResultCallback(
                                    new ResultCallback<MessageApi.SendMessageResult>() {
                                        @Override
                                        public void onResult(MessageApi.SendMessageResult resultado) {
                                            if (!resultado.getStatus().isSuccess()) {
                                               // Toast.makeText(getApplicationContext(), "Error Sincronizado.", Toast.LENGTH_LONG).show();
                                                Log.e("Sincronizado", "Error al mandar mensage. Codigo: " +
                                                        resultado.getStatus().getStatusCode());
                                            }
                                            //Toast.makeText(getApplicationContext(), "Sincronizado.", Toast.LENGTH_LONG).show();

                                        }
                                    }
                            );
                }
            }
        }).start();
    }



}
