package sunnysoft.presentapp.Interfaz;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Datos.MyAsyncTask;
import sunnysoft.presentapp.R;
import sunnysoft.presentapp.utils.RealPath;

public class RedactaremailActivity extends AppCompatActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    String url;
    String token;
    String email;
    String subdomain;
    HttpPost httppost;
    String urlv;
    String post_url;
    MultiSelectionSpinner multiSelectionSpinnerdestinatarios;
    MultiSelectionSpinner multiSelectionSpinner;

    LinearLayout layout;
    EditText et;
    TextView txtcamp;
    EditText editText3;
    EditText editText4;
    Button enviarmail;
    String[] users;
    List<String> listusu = new ArrayList<>();
    List<String> listacu = new ArrayList<>();
    List<String> listest = new ArrayList<>();
    List<Integer> listidusu = new ArrayList<>();
    List<Integer> listiddoc = new ArrayList<>();
    List<Integer[]> indi = new ArrayList<>();
    List<Integer> usuariosid = new ArrayList<>();
    List<List<String>> destinatarios = new ArrayList<>();
    List<List<Integer>> iddestinatarios = new ArrayList<>();
    // envio formulario
    Integer acu_ids[] ;
    Integer doc_ids[];
    Integer est_ids[];
    Integer usuarios[];
    Integer destinatariosids[];
    private TabLayout tabLayout;
    String nombreMenuCorreo;
    List<String> nomes;
    private Toolbar secundaria;
    private DatabaseHelper midb;
    Context context;
    private Uri mPhotoUri;
    private int request;
    private ArrayList<String> nombres = new ArrayList<>();
    private ArrayList<String> archivo = new ArrayList<>();
    private TextView txv_adj;

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
        indi.clear();
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
        multiSelectionSpinnerdestinatarios = (MultiSelectionSpinner) findViewById(R.id.mySpinnerusers3);
        editText3 = (EditText) findViewById((R.id.editText3));
        editText4 = (EditText) findViewById((R.id.editText4));
        enviarmail= (Button) findViewById(R.id.enviarmail);
        txv_adj = (TextView)findViewById(R.id.txv_adj);
        multiSelectionSpinnerdestinatarios.setListener(this, 1);
        url = "http://serverprueba.present.com.co/api/email/new";
        url += "?token=" + token;
        url += "&email=" + email;
        String seturl;
        seturl = "http://serverprueba.present.com.co/api/email/menu";
        seturl += "?token=" + token;
        seturl += "&email=" + email;
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
                        int iduser = valores.getInt("id");
                        String nombreuser = valores.getString("nombre");
                       listusu.add(nombreuser);
                       listidusu.add(iduser);

                    }
                    users = new String[listusu.size()];
                    users = listusu.toArray(users);
                    if(users.length != 0){
                        multiSelectionSpinnerdestinatarios.setItems(users);
                    }else{


                    }

                    String acgs = null;

                    acgs = user.getString("acgs");
                    //    contenidoJson es tu string conteniendo el json.
                    JSONObject mainObject = new JSONObject(acgs);
                    //Obtenemos los objetos dentro del objeto principal.
                    Iterator<String> keys = mainObject.keys();
                    while (keys.hasNext())
                    {
                        // obtiene el nombre del objeto.
                        String key = keys.next();
                        String it = mainObject.getString(key);
                        listest.add(key);
                        listacu.clear();
                        listiddoc.clear();
                        JSONArray users = new JSONArray(it);
                        for (int i = 0; i < users.length(); i++) {
                            String itemr = users.getString(i);
                            JSONObject valoresr = new JSONObject(itemr);
                            int iduser = valoresr.getInt("id");
                            String nombreuser = valoresr.getString("name");
                            listacu.add(nombreuser);
                            listiddoc.add(iduser);
                        }
                        destinatarios.add(listacu);
                        iddestinatarios.add(listiddoc);
                    }

                    String url_create = user.getString("url_create");

                    post_url = url_create;
                    post_url += "?token=" + token;
                    post_url += "&email=" + email;

                    layout = (LinearLayout) findViewById(R.id.linearLayout3);
                    int y = 1;
                    for(int x=0;x<destinatarios.size();x++) {
                        y = y+1;
                        String[] Array1;
                        Array1 = new String[destinatarios.get(x).size()];
                            Array1 = destinatarios.get(x).toArray(Array1);
                        LayoutInflater inflater = LayoutInflater.from(context);
                        int id = R.layout.layout_left;
                        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(id, null, false);
                        relativeLayout.setBackgroundResource(R.drawable.inputs_terciario);

                        /*RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
                        params.height = 70;
                        relativeLayout.setLayoutParams(params);*/



                        /*LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)relativeLayout.getLayoutParams();
                        params.setMargins(0, 0, 0, 0);
                        relativeLayout.setLayoutParams(params);*/


                        TextView txtcamp = (TextView) relativeLayout.findViewById(R.id.txvredac);
                        txtcamp.setText(listest.get(x));
                        txtcamp.setPadding(0,0,0,10);



                        multiSelectionSpinner = (MultiSelectionSpinner) relativeLayout.findViewById(R.id.mySpinneredac); ;
                        multiSelectionSpinner.setItems(Array1);
                        txtcamp.setPadding(10,0,0,10);

                        layout.addView(relativeLayout);
                        layout.setBackgroundResource(R.drawable.inputs_terciario);


                        multiSelectionSpinner.setListener(RedactaremailActivity.this, y);
                    }
                    progressDialog[0].dismiss();
                } catch (UnsupportedEncodingException e) {
                    progressDialog[0].dismiss();
                    e.printStackTrace();
                } catch (JSONException e) {
                    progressDialog[0].dismiss();
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
                    }

            }
        });

    }

    public Boolean Parsearjson() {

        httppost = new HttpPost(post_url);
        httppost.addHeader("Content-Type", "application/json");
        // validar que los arraylist del servicio >0

        Log.e("Idsusuarios", String.valueOf(indi.size()));

        for (int r = 0; r< indi.size(); r++){

            int ind = indi.get(r)[1] - 2;

            Log.e("indic", String.valueOf(iddestinatarios.get(ind).get(indi.get(r)[0])));
            //usuariosid.add(iddestinatarios.get(ind).get(indi.get(r)[0]));

        }

        Integer[] stockArr = new Integer[usuariosid.size()];
        stockArr = usuariosid.toArray(stockArr);



                destinatariosids = new Integer[multiSelectionSpinnerdestinatarios.getSelectedIndices().size()];

        for (int a = 0; a <  multiSelectionSpinnerdestinatarios.getSelectedIndices().size(); a++ ){

            destinatariosids[a]= listidusu.get(multiSelectionSpinnerdestinatarios.getSelectedIndices().get(a));

        }



        int cantidad = stockArr.length + destinatariosids.length;

        usuarios = new Integer[cantidad];

        usuarios = concatenateTwoArrays(stockArr,destinatariosids);


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


    protected Integer[] concatenateTwoArrays(Integer[] arrayFirst,Integer[] arraySecond){
        // Initialize an empty list
        List<Integer> both = new ArrayList<>();

        // Add first array elements to list
        Collections.addAll(both,arrayFirst);

        // Add another array elements to list
        Collections.addAll(both,arraySecond);

        // add 3er array


        // Convert list to array
        Integer[] result = both.toArray(new Integer[both.size()]);

        // Return the result
        return result;
    }

    public  void cargarindices(List<Integer> indices, int indicador){

        // elimina si en la nueva lista ya no viene algunos elementos y los elimina


        //int validacion = 0;


        /*for (int z = 0 ; z < indi.size(); z++) {
            validacion = 0;
            for (int w = 0 ; w < indices.size(); w++) {

                if(indices.get(w) == indi.get(z)[0] &&  indicador == indi.get(z)[1] ){
                    validacion = 1;
                    w = indices.size();
                }
            }
            if (validacion == 0){
                indi.remove(z);
            }
        }*/

        Integer aux[];

        // valida para ingresar un nuevo indice

        for (int i = 0 ; i < indices.size(); i++){
            aux = new Integer[2];
            aux[0] =  indices.get(i);
            aux[1] =  indicador;

            if(indi.size() != 0){
            for (int a = 0 ; a < indi.size(); a++) {


                if(indi.get(a)[0] != aux[0] || indi.get(a)[1] != aux[1] ){
                    indi.add(aux);
                }
            }
            }else{
                indi.add(aux);
            }
        }
        //Log.e("revision", String.valueOf(indi.size()));
    }



    @Override
    public void selectedIndices(List<Integer> indices, int multiplespinner) {

        if(multiplespinner != 1){
            cargarindices(indices, multiplespinner);
        }



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

    public void seleccionArchivo(View v){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            adjuntarArchivo();
        }else{
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED){
                adjuntarArchivo();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(0 == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                adjuntarArchivo();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void adjuntarArchivo() {
        Intent archivo = new Intent(Intent.ACTION_GET_CONTENT);
        archivo.setType("image/*");
        startActivityForResult(archivo,request=0);
    }

    public String devolverNombre(Uri uri){
        String nombre = "";
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, new String[]{
                    MediaStore.Images.ImageColumns.DISPLAY_NAME
            }, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                nombre = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                //Log.i("EL NOMBRE", nombre);
                //return nombre;
            }
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return nombre;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {
            if (requestCode == 0){
                mPhotoUri = data.getData();
                nombres.add(devolverNombre(mPhotoUri));
                archivo.add(RealPath.getRealPathFromUri(mPhotoUri,this));
                llenarTex();
            }
        }
    }


    public void llenarTex(){
        String adj = "";
        for (String item:nombres) {
            adj = adj+item+"\n";
        }
        txv_adj.setText(adj);
    }

    }
