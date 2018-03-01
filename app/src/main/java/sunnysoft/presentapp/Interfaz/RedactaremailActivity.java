package sunnysoft.presentapp.Interfaz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.guna.libmultispinner.MultiSelectionSpinner;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Datos.MyAsyncTask;
import sunnysoft.presentapp.R;

public class RedactaremailActivity extends AppCompatActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {


    String url;
    String token;
    String email;
    String subdomain;

    HttpPost httppost;
    String urlv;
    String post_url;

    // manejar los multispinner

    MultiSelectionSpinner multiSelectionSpinneracudientes;
    MultiSelectionSpinner multiSelectionSpinnerdocentes;
    MultiSelectionSpinner multiSelectionSpinnerestudiantes;

    TextView txvacudiente;
    TextView txvdocente;
    TextView txvestudiante;

    EditText editText3;
    EditText editText4;

    Button enviarmail;

    String[] Acud;
    String[] Doce;
    String[] Estu;


    List<String> listacu = new ArrayList<>();
    List<String> listdoc = new ArrayList<>();
    List<String> listest = new ArrayList<>();

    List<Integer> listidacu = new ArrayList<>();
    List<Integer> listiddoc = new ArrayList<>();
    List<Integer> listidest = new ArrayList<>();

    // envio formulario

    Integer acu_ids[] ;
    Integer doc_ids[];
    Integer est_ids[];
    Integer usuarios[];


    private TabLayout tabLayout;
    String nombreMenuCorreo;
    List<String> nomes;
    private Toolbar secundaria;


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

        secundaria = (Toolbar) findViewById(R.id.toolbar_secundaria);
        secundaria.setNavigationIcon(R.drawable.arrow_back);
        TextView titulo_secundaria = (TextView) secundaria.findViewById(R.id.toolbar_secundaria_title);
        titulo_secundaria.setText("Correos");
        secundaria.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RedactaremailActivity.this, BandejaCorreosActivity.class);
                startActivity(i);
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(RedactaremailActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });*/

        midb = new DatabaseHelper(this);
        context = this;

        Cursor Resultados = midb.Session();

        subdomain = Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token = Resultados.getString(Resultados.getColumnIndex("token"));
        email = Resultados.getString(Resultados.getColumnIndex("user"));

        multiSelectionSpinneracudientes = (MultiSelectionSpinner) findViewById(R.id.mySpinnerusers3);
        multiSelectionSpinnerdocentes = (MultiSelectionSpinner) findViewById(R.id.mySpinnerusers2);
        multiSelectionSpinnerestudiantes = (MultiSelectionSpinner) findViewById(R.id.mySpinnerusers);

        txvacudiente = (TextView) findViewById(R.id.txvacudiente);
        txvdocente = (TextView) findViewById(R.id.txvdocente);
        txvestudiante = (TextView) findViewById(R.id.txvestudiante);

        editText3 = (EditText) findViewById((R.id.editText3));
        editText4 = (EditText) findViewById((R.id.editText4));

        enviarmail= (Button) findViewById(R.id.enviarmail);

        multiSelectionSpinneracudientes.setListener(this, 1);
        multiSelectionSpinnerdocentes.setListener(this, 1);
        multiSelectionSpinnerestudiantes.setListener(this, 1);

        url = "http://serverprueba.present.com.co/api/email/new";
        url += "?token=" + token;
        url += "&email=" + email;

        String seturl;

        seturl = "http://serverprueba.present.com.co/api/email/menu";
        seturl += "?token=" + token;
        seturl += "&email=" + email;

        Log.e("urlv", seturl);

        //seteartabs(seturl);

        Desplegarcampos(url);

        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        enviar();

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
                   // String Docentes = acgs2.getString("Docentes");
                    String Acudientes = acgs2.getString("Acudientes");

                    JSONArray Acudientes2 = new JSONArray(Acudientes);

                    for (int i = 0; i < Acudientes2.length(); i++) {

                        String item22 = Acudientes2.getString(i);
                        JSONObject valores2 = new JSONObject(item22);
                        int iduser22 = valores2.getInt("id");
                        String nombreuser22 = valores2.getString("name");

                        listacu.add(nombreuser22);
                        listidacu.add(iduser22);
                    }


                  /*  JSONArray Docentes2 = new JSONArray(Docentes);

                    for (int i = 0; i < Docentes2.length(); i++) {

                        String item22 = Docentes2.getString(i);
                        JSONObject valores2 = new JSONObject(item22);
                        int iduser22 = valores2.getInt("id");
                        String nombreuser22 = valores2.getString("name");

                        listdoc.add(nombreuser22);
                        listiddoc.add(iduser22);

                    }*/



                   JSONArray Estudiantes2 = new JSONArray(Estudiantes);

                    for (int i = 0; i < Estudiantes2.length(); i++) {

                        String item2 = Estudiantes2.getString(i);
                        JSONObject valores22 = new JSONObject(item2);
                        int iduser22 = valores22.getInt("id");
                        String nombreuser22 = valores22.getString("name");
                        listest.add(nombreuser22);
                        listidest.add(iduser22);

                    }

                    String url_create = user.getString("url_create");

                    post_url = url_create;
                    post_url += "?token=" + token;
                    post_url += "&email=" + email;

                    if(listacu.size() > 0 ){

                        multiSelectionSpinneracudientes.setVisibility(View.VISIBLE);
                        txvacudiente.setVisibility(View.VISIBLE);
                        Acud = new String[listacu.size()];
                        Acud = listacu.toArray(Acud);
                        multiSelectionSpinneracudientes.setItems(Acud);

                    }else{
                        multiSelectionSpinneracudientes.setVisibility(View.INVISIBLE);
                        txvacudiente.setVisibility(View.INVISIBLE);
                    }


                    if(listdoc.size() > 0 ){

                        multiSelectionSpinnerdocentes.setVisibility(View.VISIBLE);
                        txvdocente.setVisibility(View.VISIBLE);
                        Doce = new String[listdoc.size()];
                        Doce = listacu.toArray(Doce);
                        multiSelectionSpinnerdocentes.setItems(Doce);

                    }else{
                        multiSelectionSpinnerdocentes.setVisibility(View.INVISIBLE);
                        txvdocente.setVisibility(View.INVISIBLE);
                    }

                    if(listest.size() > 0 ) {
                        multiSelectionSpinnerestudiantes.setVisibility(View.VISIBLE);
                        txvestudiante.setVisibility(View.VISIBLE);
                        Estu = new String[listest.size()];
                        Estu = listest.toArray(Estu);
                        multiSelectionSpinnerestudiantes.setItems(Estu);
                    }else{

                        multiSelectionSpinnerdocentes.setVisibility(View.INVISIBLE);
                        txvestudiante.setVisibility(View.INVISIBLE);

                    }

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

    public void  enviar(){

        enviarmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nombre = null;

                if (Parsearjson()){

                    String proceso = "Enviar Email";
                    new MyAsyncTask(RedactaremailActivity.this, httppost, proceso, urlv, nombre)
                         .execute();
                }else{
                    Toast.makeText(getApplicationContext(), "Los campos no deben estar vacios", Toast.LENGTH_SHORT).show();
                    //Log.i("VALIDACION p4","FALSE");
                }

            }
        });

    }

    public Boolean Parsearjson() {

        httppost = new HttpPost(post_url);
        httppost.addHeader("Content-Type", "application/json");
        // validar que los arraylist del servicio >0
        if(listacu.size() > 0 ){
            acu_ids = new Integer[multiSelectionSpinneracudientes.getSelectedIndices().size()];

            for (int a = 0; a <  multiSelectionSpinneracudientes.getSelectedIndices().size(); a++ ){

                acu_ids[a]= listidacu.get(multiSelectionSpinneracudientes.getSelectedIndices().get(a));

            }

        }else{

            acu_ids = new Integer[0];

        }

        if(listdoc.size() > 0 ){

            doc_ids = new Integer[multiSelectionSpinnerdocentes.getSelectedIndices().size()];

            for (int a = 0; a < multiSelectionSpinnerdocentes.getSelectedIndices().size(); a++ ){

                doc_ids[a]= listiddoc.get(multiSelectionSpinnerdocentes.getSelectedIndices().get(a));

            }
       }else{

            doc_ids = new Integer[0];

        }

        if(listest.size() > 0 ) {

            est_ids = new Integer[multiSelectionSpinnerestudiantes.getSelectedIndices().size()];

            for (int a = 0; a < multiSelectionSpinnerestudiantes.getSelectedIndices().size(); a++ ){

                est_ids[a]= listidest.get(multiSelectionSpinnerestudiantes.getSelectedIndices().get(a));

            }

        }else{
            est_ids = new Integer[0];
        }

        int cantidad = acu_ids.length + doc_ids.length + est_ids.length;

        usuarios = new Integer[cantidad];

        usuarios = concatenateTwoArrays(acu_ids,doc_ids,est_ids);





        // parsear json

        JSONArray mJSONArrayusuarios = new JSONArray(Arrays.asList(usuarios));


        JSONObject j = new JSONObject();
        //j.put("key","users_ids");
        try {
            j.put("email", email);
            j.put("token", token);
            j.put("usuarios", mJSONArrayusuarios);
            j.put("title", editText3.getText());
            j.put("contents", editText4.getText());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(j.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringEntity.setContentType((Header) new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httppost.setEntity(stringEntity);
        //Log.e("Mensaje", String.valueOf(j.toString()));
        //Log.i("VALIDACION P3","TRUE");
        return true;

        // armar cadena y enviar
    }


    protected Integer[] concatenateTwoArrays(Integer[] arrayFirst,Integer[] arraySecond, Integer[] arraytercer){
        // Initialize an empty list
        List<Integer> both = new ArrayList<>();

        // Add first array elements to list
        Collections.addAll(both,arrayFirst);

        // Add another array elements to list
        Collections.addAll(both,arraySecond);

        // add 3er array

        Collections.addAll(both,arraytercer);

        // Convert list to array
        Integer[] result = both.toArray(new Integer[both.size()]);

        // Return the result
        return result;
    }



    @Override
    public void selectedIndices(List<Integer> indices, int multiplespinner) {

        //Toast.makeText(this, indices.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void selectedStrings(List<String> strings) {

        //Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();

    }

    /*public void seteartabs(final String url){

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;

        tabLayout = (TabLayout) findViewById(R.id.tabsent);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        nomes = new ArrayList<>();

        // llamado del servicio
        RequestHandle post  = client.get(url, new AsyncHttpResponseHandler() {


            @Override
            public void onStart(){

                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                // Declaracion de variables
                String responseStr = null;

                try {

                    //respuesta del servicio
                    responseStr = new String(responseBody, "UTF-8");
                    // manejo del primer nivel de objetos
                    JSONObject user = new JSONObject(responseStr);
                    // Se obtiene valores del objeto
                    String valorLlave = user.getString("menu");

                    JSONArray items = new JSONArray(valorLlave);

                    for(int i=0; i < items.length(); i++) {
                        String item = items.getString(i);

                        JSONObject valores = new JSONObject(item);

                        nombreMenuCorreo = valores.getString("name");
                        nomes.add(nombreMenuCorreo);
                    }

                    for (int a = 0; a< nomes.size(); a++){
                        /////////////////////creacion de tabs dinamicamente//////////////////////
                        tabLayout.addTab(tabLayout.newTab().setText(nomes.get(a)));
                    }

                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            Intent i = new Intent(getApplicationContext(),BandejaCorreosActivity.class);
                            i.putExtra("posicion",tab.getPosition());
                            startActivity(i);
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });

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
                        for(int i=0; i < jsonarray.length(); i++) {
                            String mensaje = jsonarray.getString(i);
                            Toast.makeText(RedactaremailActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }

                        midb.logouth();
                        midb.oncreateusers();
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

                    //Institución no valida.
                }


            }

        });
    }*/

    // fin clase

    }
