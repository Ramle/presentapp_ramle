package sunnysoft.presentapp.Interfaz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.R;

public class RedactaremailActivity extends AppCompatActivity {


    String url;
    String token;
    String email;
    String subdomain;
    String nombre;


    HttpPost httppost;
    String urlv;


    private DatabaseHelper midb;

    Context context;

    @Override
    public void onBackPressed() {
        Toast.makeText(RedactaremailActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redactaremail);

        //Tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getText(R.string.txt_menu_Temail));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(RedactaremailActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });

        midb = new DatabaseHelper(this);
        context = this;

        Cursor Resultados = midb.Session();

        subdomain = Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token = Resultados.getString(Resultados.getColumnIndex("token"));
        email = Resultados.getString(Resultados.getColumnIndex("user"));

        url = "http://serverprueba.present.com.co/api/email/new";
        url += "?token=" + token;
        url += "&email=" + email;

        Log.e("url", url);

        Desplegarcampos(url);

        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void Desplegarcampos(String url) {

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;

        // llamado del servicio
        RequestHandle post = client.get(url, new AsyncHttpResponseHandler() {

            // Declaracion de variables
            String responseStr = null;

            final ProgressDialog[] progressDialog = new ProgressDialog[1];

            @Override
            public void onStart(){

                super.onStart();
                progressDialog[0] = ProgressDialog.show(
                        context, "Por favor espere", "Procesando...");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String campos;

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    JSONObject user = new JSONObject(responseStr);
                    String usuarios = user.getString("usuarios");
                    JSONArray items = new JSONArray(usuarios);
                   for (int i = 0; i < items.length(); i++) {

                        String item = items.getString(i);

                        JSONObject valores = new JSONObject(item);
                        String iduser = valores.getString("id");
                        String nombreuser = valores.getString("nombre");

                    }

                    String acgs = user.getString("acgs");

                    JSONObject acgs2 = new JSONObject(acgs);
                    String Estudiantes = acgs2.getString("Estudiantes");
                    //String Docentes = acgs2.getString("Docentes");
                    String Acudientes = acgs2.getString("Acudientes");



                   JSONArray Estudiantes2 = new JSONArray(Estudiantes);

                    for (int i = 0; i < Estudiantes2.length(); i++) {

                        String item2 = Estudiantes2.getString(i);
                        JSONObject valores22 = new JSONObject(item2);
                        String iduser22 = valores22.getString("id");
                        String nombreuser22 = valores22.getString("name");

                    }


                    JSONArray Acudientes2 = new JSONArray(Acudientes);

                    for (int i = 0; i < Acudientes2.length(); i++) {

                        String item22 = Acudientes2.getString(i);
                        JSONObject valores2 = new JSONObject(item22);
                        String iduser22 = valores2.getString("id");
                        String nombreuser22 = valores2.getString("name");

                    }

                    String url_create = user.getString("url_create");

                    progressDialog[0].dismiss();



                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                if (statusCode == 421) {
                    //declaracion de variables
                    String responseStr = null;
                    progressDialog[0].dismiss();

                    try {

                        // respuesta del servicio
                        responseStr = new String(responseBody, "UTF-8");
                        // manejo de primer nivel de objetos del json
                        JSONObject errorx = new JSONObject(responseStr);
                        // se obtiene los valores del json
                        String valorLlave = errorx.getString("errors");
                        // manejo del segundo nivel de valores de json
                        JSONObject errorxa = new JSONObject(valorLlave);
                        // se obtiene los valores que contiene el objeto
                        String msgerror = errorxa.getString("login");
                        // se maneja el array json
                        JSONArray jsonarray = new JSONArray(msgerror);

                        //se obtiene cada uno de los mensajes que se encuentran dentro del json
                        for (int i = 0; i < jsonarray.length(); i++) {
                            String mensaje = jsonarray.getString(i);
                            Toast.makeText(RedactaremailActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }
                        midb.logouth();
                        midb.oncreateusers();
                        progressDialog[0].dismiss();
                        Intent i = new Intent(RedactaremailActivity.this, InicioActivity.class);
                        startActivity(i);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(RedactaremailActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(RedactaremailActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    // fin clase

    }
