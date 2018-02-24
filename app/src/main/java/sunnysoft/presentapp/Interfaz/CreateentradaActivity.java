package sunnysoft.presentapp.Interfaz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import me.gujun.android.taggroup.TagGroup;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Datos.MyAsyncTask;
import sunnysoft.presentapp.R;

import static android.graphics.Typeface.BOLD;
import static sunnysoft.presentapp.R.dimen.alto_inputs;

public class CreateentradaActivity extends AppCompatActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    String url;
    String token;
    String email;
    String subdomain;
    String nombre;

    // arrays para pasar al multiselection spinner

    String[] Users;
    String[] Tags;
    String[] Campos;

    // manejar los multispinner

    MultiSelectionSpinner multiSelectionSpinnercampos;
    MultiSelectionSpinner multiSelectionSpinnerusers;
    MultiSelectionSpinner multiSelectionSpinnertags;

    // Array list de campos
    List<Integer> listidcamp = new ArrayList<>();
    List<String> listcamp = new ArrayList<>();

    // Array list de usuarios

    List<Integer> listidusers = new ArrayList<>();
    List<String> listusers = new ArrayList<>();

    // Array list de tags

    List<Integer> listidtags = new ArrayList<>();
    List<String> listtags = new ArrayList<>();

    //Array list de campos a pintar

    List<Integer> indcamp = new ArrayList<>();
    List<Integer> pintidcamp = new ArrayList<>();

    // validaciones
    int indicador = 0;
    List<Integer> camppin = new ArrayList<>();


    //campos
    LinearLayout layout;
    EditText et;
    TextView txtcamp;
    Button btncrear;
    Button btncre;

    //tags
    TagGroup mTagGroup;
    TagGroup mTagGroup2;

    //Datos formulario
    int id;
    int acg_id;
    String name;
    String post_url;

    // envio formulario

    Integer users_ids[] ;
    Integer tags_ids[];
    Integer fields_ids[];
    String fields_content[];


    private DatabaseHelper midb;
    private Toolbar secundaria;

    HttpPost httppost;
    String urlv;

    Context context;

    @Override
    public void onBackPressed() {
        Toast.makeText(CreateentradaActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createentrada);

        midb = new DatabaseHelper(this);
        context = this;

        Cursor Resultados = midb.Session();

        //Tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getText(R.string.txt_menu_centradas));
        toolbar_title.setTextColor(getResources().getColor(R.color.color_letra_in_prim));
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(CreateentradaActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });

        subdomain = Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token = Resultados.getString(Resultados.getColumnIndex("token"));
        email = Resultados.getString(Resultados.getColumnIndex("user"));

        Bundle datos = getIntent().getExtras();
        urlv = datos.getString("url");
        nombre = datos.getString("nombre");


        url = datos.getString("url");
        url += "?token=" + token;
        url += "&email=" + email;


        tags_ids = new Integer[0];


        Log.e("url",url);

        mTagGroup = (TagGroup) findViewById(R.id.tag_group);

        mTagGroup2 = (TagGroup) findViewById(R.id.tag_group2);

        multiSelectionSpinnercampos = (MultiSelectionSpinner) findViewById(R.id.mySpinnercampos);
        multiSelectionSpinnerusers = (MultiSelectionSpinner) findViewById(R.id.mySpinnerusers);
        multiSelectionSpinnertags = (MultiSelectionSpinner) findViewById(R.id.mySpinnertags);

        btncre = (Button) findViewById(R.id.btncrea);

         Desplegarcampos(url);

        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        multiSelectionSpinnercampos.setListener(this, 1);
        multiSelectionSpinnerusers.setListener(this, 2);
        multiSelectionSpinnertags.setListener(this, 3);

        enviar();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectedIndices(List<Integer> indices, int multispinner) {
        switch (multispinner) {
            case 1:
                indicador = indicador + 1;
                pintarcampos((List<Integer>) indices, indicador);
                break;
            case 2:
                pintartagsuser((List<Integer>) indices);

                break;
            case 3:
                pintartagtags((List<Integer>) indices);
                break;
        }

    }

    @Override
    public void selectedStrings(List<String> strings) {

        //Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();

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

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    Campos = new String[listcamp.size()];
                    Users = new String[listusers.size()];
                    Tags = new String[listtags.size()];

                    JSONObject user = new JSONObject(responseStr);
                    // Se obtiene valores del objeto

                    String formulario = user.getString("proceso");
                    JSONObject caracteristicas = new JSONObject(formulario);
                    id = caracteristicas.getInt("id");
                    acg_id = caracteristicas.getInt("acg_id");
                    name = caracteristicas.getString("name");
                    post_url = user.getString("post_url");
                    post_url += "?token=" + token;
                    post_url += "&email=" + email;


                    String valorLlave = user.getString("fields");
                    JSONArray items = new JSONArray(valorLlave);

                    for (int i = 0; i < items.length(); i++) {

                        String item = items.getString(i);

                        JSONObject valores = new JSONObject(item);

                        listcamp.add(valores.getString("name"));
                        listidcamp.add(valores.getInt("id"));

                    }

                    String vrllve = user.getString("users");
                    JSONArray users = new JSONArray(vrllve);

                    for (int a = 0; a < users.length(); a++) {

                        String item2 = users.getString(a);

                        JSONObject valores2 = new JSONObject(item2);

                        listusers.add(valores2.getString("name"));
                        listidusers.add(valores2.getInt("id"));

                    }


                    String vrtags = user.getString("tags");
                    JSONArray tags = new JSONArray(vrtags);

                    for (int i = 0; i < tags.length(); i++) {

                        String item3 = tags.getString(i);

                        JSONObject valores3 = new JSONObject(item3);

                        listtags.add(valores3.getString("name"));
                        listidtags.add(valores3.getInt("id"));

                    }

                    Campos = listcamp.toArray(Campos);
                    Users = listusers.toArray(Users);
                    Tags = listtags.toArray(Tags);



                    multiSelectionSpinnercampos.setItems(Campos);
                    multiSelectionSpinnerusers.setItems(Users);
                    multiSelectionSpinnertags.setItems(Tags);



                    ///////////////////////segunda toolbar /////////////////////
                    secundaria = (Toolbar) findViewById(R.id.toolbar_secundaria);
                    secundaria.setNavigationIcon(R.drawable.arrow_back);
                    TextView titulo_secundaria = (TextView) secundaria.findViewById(R.id.toolbar_secundaria_title);
                    String label = name +" | " + nombre;
                    titulo_secundaria.setText(label);
                    secundaria.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(CreateentradaActivity.this, ModulosActivity.class);
                            startActivity(i);
                        }
                    });

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
                            Toast.makeText(CreateentradaActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }
                        midb.logouth();
                        midb.oncreateusers();
                        Intent i = new Intent(CreateentradaActivity.this, InicioActivity.class);
                        startActivity(i);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(CreateentradaActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(CreateentradaActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    public void pintarcampos(List<Integer> indcamp, int indicador) {

        int codigocampo;
        String nomcampos;
        Boolean containt;
        Boolean containt2;
        int estaticos = 8;


        ArrayList<Integer> Indices = new ArrayList<>();
        ArrayList<Integer> Auxiliar = new ArrayList<>();

        ArrayList<Integer> removes = new ArrayList<>();
        Indices.clear();

        btncre.setVisibility(View.GONE);


        Indices.addAll(indcamp);
        if (indicador == 1) {
            layout = (LinearLayout) findViewById(R.id.layout_edittext);
            camppin.clear();

            for (int c = 0; c < Indices.size(); c++) {

                codigocampo = listidcamp.get(Indices.get(c));
                nomcampos = listcamp.get(Indices.get(c));
                camppin.add(codigocampo);

                txtcamp = new TextView(this);
                txtcamp.setText(nomcampos);
                txtcamp.setTypeface(null, Typeface.BOLD);

                LinearLayout.LayoutParams paramo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                paramo.setMargins(0,50,0,0);
                txtcamp.setLayoutParams(paramo);


                et = new EditText(this);
                et.setBackgroundResource(R.drawable.inputs_secundarios);
                et.setId(codigocampo);
                //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(520, ViewGroup.LayoutParams.WRAP_CONTENT);

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                param.setMargins(0,50,0,0);
                et.setLayoutParams(param);
                //et.setLayoutParams(params);

                layout.addView(txtcamp);
                layout.addView(et);

            }

            btncrear = new Button(this);
            btncrear.setText("Crear");
            btncrear.setBackgroundResource(R.drawable.botones_secundarios);
            btncrear.setTextColor(getResources().getColor(R.color.color_letra_btn_prim));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            //params.setLayoutDirection(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
            params.setMargins(800,50,0,20);
            btncrear.setLayoutParams(params);
            btncrear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    fields_ids = new Integer[camppin.size()];
                    fields_content = new String[camppin.size()];
                    fields_ids = camppin.toArray(fields_ids);

                    for (int t = 0; t<fields_ids.length; t++){
                        EditText campo = (EditText)findViewById(fields_ids[t]);
                        String content = campo.getText().toString();
                        fields_content[t]=content;
                    }
                    if (Parsearjson()){
                        String proceso = "Crear Entrada";
                        new MyAsyncTask(CreateentradaActivity.this, httppost, proceso, urlv , nombre)
                                .execute();
                    }else{
                        Toast.makeText(getApplicationContext(), "Los campos no deben estar vacios", Toast.LENGTH_SHORT).show();
                    }


                }
            });
            layout.addView(btncrear);

        } else {
            layout = (LinearLayout) findViewById(R.id.layout_edittext);
            Auxiliar.clear();
            removes.clear();
            int y = 0;

            for (int d = 0; d < Indices.size(); d++) {
                Auxiliar.add(listidcamp.get(Indices.get(d)));
            }
            for (int e = camppin.size(); e > 0; e--) {
                y = e - 1;
                containt2 = Auxiliar.contains(camppin.get(y));
                if (containt2 == false) {
                    int x = y * 2;
                    int removetxt = estaticos + x;
                    int removeedt = removetxt + 1;
                    layout.removeViewAt(removeedt);
                    layout.removeViewAt(removetxt);
                    removes.add(e);
                }
            }


            for (int f = 0; f < removes.size(); f++) {
                camppin.remove(removes.get(f) - 1);
            }
            int cantidad = (camppin.size() * 2) + 8;
            layout.removeViewAt(cantidad);

            for (int c = 0; c < Indices.size(); c++) {
                containt = camppin.contains(listidcamp.get(Indices.get(c)));

                if (containt == false) {
                    codigocampo = listidcamp.get(Indices.get(c));
                    nomcampos = listcamp.get(Indices.get(c));
                    camppin.add(codigocampo);

                    txtcamp = new TextView(this);
                    txtcamp.setText(nomcampos);
                    txtcamp.setTypeface(null, Typeface.BOLD);

                    LinearLayout.LayoutParams paramo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramo.setMargins(0,50,0,0);
                    txtcamp.setLayoutParams(paramo);


                    et = new EditText(this);
                    et.setBackgroundResource(R.drawable.inputs_secundarios);
                    et.setId(codigocampo);
                    //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(520, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    param.setMargins(0,50,0,0);
                    et.setLayoutParams(param);

                    layout.addView(txtcamp);
                    layout.addView(et);

                }

            }


            btncrear = new Button(this);
            btncrear.setBackgroundResource(R.drawable.botones_secundarios);
            btncrear.setTextColor(getResources().getColor(R.color.color_letra_btn_prim));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            //params.setLayoutDirection(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
            params.setMargins(800,50,0,20);
            btncrear.setText("Crear");
            btncrear.setLayoutParams(params);

            btncrear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    fields_ids = new Integer[camppin.size()];
                    fields_content = new String[camppin.size()];
                    fields_ids = camppin.toArray(fields_ids);

                    for (int t = 0; t<fields_ids.length; t++){
                        EditText campo = (EditText)findViewById(fields_ids[t]);
                        String content = campo.getText().toString();
                        fields_content[t]=content;
                    }
                    if (Parsearjson()){
                        String proceso = "Crear Entrada";
                        new MyAsyncTask(CreateentradaActivity.this, httppost, proceso, urlv, nombre)
                                .execute();
                    }else{
                        Toast.makeText(getApplicationContext(), "Los campos no deben estar vacios", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            layout.addView(btncrear);

        }

    }

    public void pintartagsuser(List<Integer> indtag) {


        ArrayList<Integer> Indices = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<Integer>  idusers = new ArrayList<>();
        int codigotag;
        String nomtag;

        String[] nomtags;
        Indices.clear();
        Indices.addAll(indtag);

        for (int c = 0; c < Indices.size(); c++) {

            nomtag = listusers.get(Indices.get(c));
            codigotag = listidusers.get(Indices.get(c));
            tags.add(nomtag);
            idusers.add(codigotag);
        }
        nomtags = new String[tags.size()];
        users_ids = new Integer[tags.size()];
        nomtags = tags.toArray(nomtags);
        users_ids = idusers.toArray(users_ids);
        mTagGroup.setTags(nomtags);


    }

    public void pintartagtags(List<Integer> indtag) {


        ArrayList<Integer> Indices = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<Integer>  idtags = new ArrayList<>();
        int codigotag;
        String nomtag;

        String[] nomtags;
        Indices.clear();
        Indices.addAll(indtag);

        for (int c = 0; c < Indices.size(); c++) {

            nomtag = listtags.get(Indices.get(c));
            codigotag = listidtags.get(Indices.get(c));
            tags.add(nomtag);
            idtags.add(codigotag);
        }
        nomtags = new String[tags.size()];
        tags_ids = new Integer[tags.size()];
        nomtags = tags.toArray(nomtags);
        tags_ids = idtags.toArray(tags_ids);
        mTagGroup2.setTags(nomtags);

    }

    public Boolean Parsearjson() {

        httppost = new HttpPost(post_url);
        httppost.addHeader("Content-Type", "application/json");
        Log.i("DATOS",""+multiSelectionSpinnercampos.getSelectedIndices().size());
        Log.i("DATOS",""+multiSelectionSpinnertags.getSelectedIndices().size());
        Log.i("DATOS",""+multiSelectionSpinnerusers.getSelectedIndices().size());
        try {
            if (multiSelectionSpinnercampos.getSelectedIndices().size() == 0) {
                Log.i("VALIDACION P1","FALSE");
                return false;
            }else {
                if (users_ids == null) {
                    Log.i("VALIDACION P2","FALSE");
                    return false;
                } else {
                    JSONArray mJSONArrayusersid = new JSONArray(Arrays.asList(users_ids));
                    JSONArray mJSONArraytagsid = new JSONArray(Arrays.asList(tags_ids));
                    JSONArray mJSONArraysfieldsid = new JSONArray(Arrays.asList(fields_ids));
                    JSONArray mJSONArraysfieldscontent = new JSONArray(Arrays.asList(fields_content));

                    JSONObject j = new JSONObject();
                    //j.put("key","users_ids");
                    j.put("users_ids", mJSONArrayusersid);
                    j.put("tags_ids", mJSONArraytagsid);
                    j.put("fields_ids", mJSONArraysfieldsid);
                    j.put("fields_content", mJSONArraysfieldscontent);

                    StringEntity stringEntity = new StringEntity(j.toString());
                    stringEntity.setContentType((Header) new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httppost.setEntity(stringEntity);
                    Log.i("VALIDACION P3","TRUE");
                    return true;
                }
            }

            //forma el JSON y tipo de contenido

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    void enviar(){

        btncre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Parsearjson()){
                    Log.i("VALIDACION p4","TRUE");
                    fields_ids = new Integer[camppin.size()];
                    fields_content = new String[camppin.size()];
                    fields_ids = camppin.toArray(fields_ids);

                    for (int t = 0; t<fields_ids.length; t++){
                        EditText campo = (EditText)findViewById(fields_ids[t]);
                        String content = campo.getText().toString();
                        fields_content[t]=content;
                    }
                    String proceso = "Crear Entrada";
                    new MyAsyncTask(CreateentradaActivity.this, httppost, proceso, urlv, nombre)
                            .execute();
                }else{
                    Toast.makeText(getApplicationContext(), "Los campos no deben estar vacios", Toast.LENGTH_SHORT).show();
                    Log.i("VALIDACION p4","FALSE");
                }
            }
        });

    }


}



