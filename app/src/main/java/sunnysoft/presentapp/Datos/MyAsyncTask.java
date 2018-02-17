package sunnysoft.presentapp.Datos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.client.methods.HttpPost;
import sunnysoft.presentapp.Interfaz.CreareventoActivity;
import sunnysoft.presentapp.Interfaz.CreateentradaActivity;

/**
 * Created by dchizavo on 9/12/17.
 */

public class MyAsyncTask extends AsyncTask<String,Void,String> {

    private ProgressDialog progressDialog;
    private Context context;
    HttpPost httppost;
    String proceso;
    String urlv;
    String nombre;
    Intent i;


    /**Constructor de clase */
    public MyAsyncTask(Context context, HttpPost httppost, String proceso, String urlv, String nombre ) {
        this.context = context;
        this.httppost = httppost;
        this.proceso = proceso;
        this.urlv= urlv;
        this.nombre = nombre;

    }


    @Override
    protected String doInBackground(String... params) {

        datosentradas myRestFulGP = new datosentradas();
        String resultado=null;

        try {

            switch (proceso){
                case "Crear Entrada":

                    resultado = myRestFulGP.enviardatos(httppost);
                break;
                case "Crear Evento":

                    resultado = myRestFulGP.enviardatos(httppost);
                    break;
                default:
                     resultado = null;
                    break;
            }
        }catch (Exception e){
            return null;
        }
        return resultado;

    }

    /**
     * Antes de comenzar la tarea muestra el progressDialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(
                context, "Por favor espere", "Procesando...");
    }

    /**
     * Cuando se termina de ejecutar, cierra el progressDialog y retorna el resultado a la interfaz
     * **/
    @Override
    protected void onPostExecute(String resul) {
        progressDialog.dismiss();


        switch (proceso){
            case "Crear Entrada":

                Toast.makeText(context, "La entrada se ha registrado", Toast.LENGTH_LONG).show();
                i = new Intent(context, CreateentradaActivity.class);
                i.putExtra("url", urlv);
                i.putExtra("nombre", nombre);
                context.startActivity(i);
                break;

            case "Crear Evento":

                Toast.makeText(context, "La evento se ha registrado", Toast.LENGTH_LONG).show();
                i = new Intent(context, CreareventoActivity.class);

                context.startActivity(i);
                break;

        }

    }



}
