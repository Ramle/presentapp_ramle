package sunnysoft.presentapp.Interfaz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import me.gujun.android.taggroup.TagGroup;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Interfaz.adapter.FieldsEntradasAdapter;
import sunnysoft.presentapp.Interfaz.adapter.FieldsEventoAdapter;
import sunnysoft.presentapp.Interfaz.pojo.FieldsEntradas;
import sunnysoft.presentapp.Interfaz.pojo.FieldsEvento;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.CardView;
import sunnysoft.presentapp.R;

public class DetalleentradagralActivity extends AppCompatActivity {

    private DatabaseHelper midb;
    String url;
    //String urlT;
    TextView nombreentrada;
    TextView cursogrupo;
    TextView txv_nombre;
    TextView txv_fecha;
    TextView fechainicioentrada;
    TextView horainicioentrada;
    TextView dataresponsables;
    ImageView img_persona;
    String email;
    private Toolbar secundaria;
    //private TabLayout tabLayout;
    final ProgressDialog[] progressDialog = new ProgressDialog[1];
    //List<String>nomesImage = new ArrayList<>();
    //int notification_count;

    String token;
    String subdomain;
    private HashMap<String, FieldsEntradas> fieldsEntradas = new HashMap<>();


    @Override
    public void onBackPressed() {
        Toast.makeText(DetalleentradagralActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalleentradagral);

        Bundle datos = getIntent().getExtras();

        if (datos != null){
         url = datos.getString("url");
         //urlT = datos.getString("tURL");
        }
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getText(R.string.txt_menu_entradas));
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        secundaria = (Toolbar) findViewById(R.id.toolbar_secundaria);
        secundaria.setNavigationIcon(R.drawable.arrow_back);
        TextView titulo_secundaria = (TextView) secundaria.findViewById(R.id.toolbar_secundaria_title);
        titulo_secundaria.setText("Entradas");
        secundaria.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetalleentradagralActivity.this, EntradasActivity.class);
                startActivity(i);
            }
        });

        midb = new DatabaseHelper(this);

        Cursor Resultados = midb.Session();

        /*try {

            seteartabs(urlT);

        }catch(Exception e){

            Toast.makeText(DetalleentradagralActivity.this, "Fallo por NN ", Toast.LENGTH_LONG).show();

        }*/

        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));

        url += "?token="+token;
        url += "&email="+ email;


        //Toast.makeText(DetalleentradagralActivity.this, "Fallo por a"+url, Toast.LENGTH_LONG).show();

        Log.e("url",url);
        final ListView mfieldsEntradasList;
        //final DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        //final DateFormat originalFormatHour = new SimpleDateFormat("HH:mm:ss");

        final TagGroup mTagGroup = (TagGroup) findViewById(R.id.tag_group);
        mfieldsEntradasList = (ListView) findViewById(R.id.fieldeventos_list);

        //traer datos de ws
        final AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;


        nombreentrada = (TextView) findViewById(R.id.txv_proceso);
        cursogrupo = (TextView) findViewById(R.id.cursogrupo);
        txv_nombre = (TextView) findViewById(R.id.txv_nombre);
        txv_fecha = (TextView) findViewById(R.id.txv_fecha);
        fechainicioentrada = (TextView) findViewById(R.id.fechainicioentrada);
        horainicioentrada = (TextView) findViewById(R.id.horainicioentrada);
        dataresponsables = (TextView) findViewById(R.id.dataresponsables);
        img_persona = (ImageView) findViewById(R.id.img_persona);

        final LayoutInflater inflater = LayoutInflater.from(this);


        // Invoke RESTful Web Service with Http parameters
        RequestHandle post = client.get(DetalleentradagralActivity.this, url, entity, "application/json", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                //mydb.borrar_Users();
                //mydb.oncreateusers();

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                Event ev1 = null;
                // called when response HTTP status is "200 OK"
                String responseStr = null;
                String id = null;
                String proceso_name = null;
                String curso_grupo = null;
                String user_name = null;
                String user_image = null;
                String name = null;
                String nameField = null;
                String contentField = null;
                String created_at = null;
                String horaevento = null;
                String detail_url = null;
                String time_string = null;
                String date_string = null;
                String created_by_name = null;
                String tipo_persona = null;

                try {

                    responseStr = new String(responseBody, "UTF-8");
                    JSONObject jsonobject = new JSONObject(responseStr);
                    Iterator x = jsonobject.keys();
                    // id = jsonobject.getString("id");
                    proceso_name = jsonobject.getString("proceso_name");
                    curso_grupo = jsonobject.getString("curso_grupo");
                    user_name = jsonobject.getString("user_name");
                    created_at = jsonobject.getString("created_at");
                    user_image = jsonobject.getString("user_image");

                    created_by_name  = jsonobject.getString("created_by_name");
                    JSONArray jsonarray = new JSONArray(jsonobject.getString("tags"));
                    JSONArray jsonarray_field = new JSONArray(jsonobject.getString("fields"));

                    txv_nombre.setText(user_name);
                    txv_fecha.setText(curso_grupo);
                    nombreentrada.setText(proceso_name);
                    dataresponsables.setText(created_by_name);

                    //descarga de imagen logo del colegio
                    new DetalleentradagralActivity.DownloadImage().execute(user_image);
                    String [] tags = new String [jsonarray.length()];
                    String [] fields = new String [jsonarray.length()];

                    for(int i=0; i < jsonarray.length(); i++) {

                        JSONObject jsonobject_tags = jsonarray.getJSONObject(i);
                        name       = jsonobject_tags.getString("name");
                        tags[i] = name;

                        // Toast.makeText(VereventoActivity.this, "Fallo por "+name, Toast.LENGTH_LONG).show();
                    }

                    mTagGroup.setTags(tags);
                    LinearLayout lin = (LinearLayout)findViewById(R.id.contenedor);

                    for(int j=0; j < jsonarray_field.length(); j++) {

                        final FieldsEntradasAdapter mfieldsEntradaAdapter;

                        JSONObject jsonobject_fields = jsonarray_field.getJSONObject(j);
                        nameField       = jsonobject_fields.getString("name");
                        contentField       = jsonobject_fields.getString("content");

                        /////////ACA///////////////
                        Log.i("TITUTLO",nameField);
                        Log.i("CONTENIDO",contentField);
                        View v = inflater.inflate(R.layout.list_items_verfieldsentradas,lin,true);
                        TextView titulo = (TextView) v.findViewById(R.id.campotitulo);
                        TextView contenido = (TextView)v.findViewById(R.id.campodetalle);
                        titulo.setId(j);
                        contenido.setId(j+1);
                        titulo.setText(nameField);
                        contenido.setText(contentField);
                        /////////////////////////////

                        /*saveFieldEntradas(new FieldsEntradas(nameField, contentField, j + 1));


                        // Inicializar el adaptador con la fuente de datos.
                        mfieldsEntradaAdapter = new FieldsEntradasAdapter(DetalleentradagralActivity.this,getfieldsEntradas());

                        //Relacionando la lista con el adaptador
                        mfieldsEntradasList.setAdapter(mfieldsEntradaAdapter);

                        ////////////////////////////////////////////////////////////

                        ListAdapter adapter = mfieldsEntradasList.getAdapter();

                        int totalHeight = mfieldsEntradasList.getPaddingTop() + mfieldsEntradasList.getPaddingBottom();

                        for (int i=0; i<adapter.getCount();i++){
                            View listItem = adapter.getView(i,null,mfieldsEntradasList);
                            if (listItem instanceof ViewGroup){
                                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                            }
                            listItem.measure(0,0);
                            totalHeight += listItem.getMeasuredHeight();
                        }
                        ViewGroup.LayoutParams params = mfieldsEntradasList.getLayoutParams();
                        params.height = totalHeight + (mfieldsEntradasList.getDividerHeight() * (mfieldsEntradaAdapter.getCount() - 1));
                        mfieldsEntradasList.setLayoutParams(params);
*/
                    }



                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                    Toast.makeText(DetalleentradagralActivity.this, "Fallo por a", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DetalleentradagralActivity.this, "Fallo por "+e, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                if (statusCode == 404) {
                    Log.i("On Failure", "404");
                    Toast.makeText(DetalleentradagralActivity.this, "Fallo por 404 ", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Log.i("On Failure", "500");
                    Toast.makeText(DetalleentradagralActivity.this, "Fallo por 500 ", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(DetalleentradagralActivity.this, "Fallo por NN ", Toast.LENGTH_LONG).show();
                }

            }

        });

        //Tooblar
        /*Toolbar toolbarfecha = (Toolbar) findViewById(R.id.toolbarfecha);
        TextView toolbar_titlefecha = (TextView)toolbarfecha.findViewById(R.id.toolbar_titlefecha);
        setSupportActionBar(toolbarfecha);*/
        //toolbar_titlefecha.setText("Ver Entrada");
        //getSupportActionBar().setDisplayShowTitleEnabled(false);


    }

    // funciones de descarga de imagenes
    private void setImage(Drawable drawable) {

        setBackgroundDrawable(drawable);
    }

    @Deprecated
    public void setBackgroundDrawable(Drawable drawable) {

        img_persona.setBackgroundDrawable(drawable);
    }


    /////////////////////llenar tabs ///////////////////////////////////////////
    /*Public void seteartabs(String url){

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;

        tabLayout = (TabLayout) findViewById(R.id.tabsent);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        // llamado del servicio
        RequestHandle post  = client.get(url, new AsyncHttpResponseHandler() {


            @Override
            public void onStart(){

                super.onStart();
                progressDialog[1] = ProgressDialog.show(
                        getApplicationContext(), "Por favor espere", "Procesando...");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                // Declaracion de variables
                String responseStr = null;

                //respuesta del servicio
                try {
                    responseStr = new String(responseBody, "UTF-8");
                    // manejo del primer nivel de objetos

                    JSONArray items = new JSONArray(responseStr);

                    for(int i=0; i < items.length(); i++) {

                        String item = items.getString(i);

                        JSONObject valores = new JSONObject(item);

                        String image_user = valores.getString("image");
                        notification_count = valores.getInt("notification_count");
                        nomesImage.add(image_user);

                    }

                    for (int a = 0; a< nomesImage.size(); a++){
                        /////////////////////creacion de tabs dinamicamente//////////////////////
                        new DetalleentradagralActivity.DownloadImageTab().execute(nomesImage.get(a));
                    }
                    tabLayout.setScrollPosition(2,0f,true);


                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            Intent i = new Intent(getApplicationContext(),EntradasActivity.class);
                            i.putExtra("posicion",tab.getPosition());
                            startActivity(i);
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });

                    //progressDialog[0].dismiss();

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
                            Toast.makeText(DetalleentradagralActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }

                        midb.logouth();
                        midb.oncreateusers();
                        Intent i = new Intent(DetalleentradagralActivity.this, InicioActivity.class);
                        startActivity(i);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // When Http response code is '500'
                else if (statusCode == 500) {

                    Toast.makeText(DetalleentradagralActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(DetalleentradagralActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

                    //Institución no valida.
                }

            }

        });

    }*/

    public class DownloadImage extends AsyncTask<String, Integer, Drawable> {
        @Override
        protected Drawable doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0]);
        }

        protected void onPostExecute(Drawable image)
        {
            setImage(image);
        }

        private Drawable downloadImage(String _url)
        {
            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            //BufferedInputStream buf;
            try {
                url = new URL(_url);
                in = url.openStream();

                // Read the inputstream
                buf = new BufferedInputStream(in);

                // Convert the BufferedInputStream to a Bitmap
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }

                return new BitmapDrawable(bMap);

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }
    }


    /*public class DownloadImageTab extends AsyncTask<String, Integer, Drawable> {
        @Override
        protected Drawable doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0]);
        }

        protected void onPostExecute(Drawable image)
        {
            setImageTab(image);
        }

        private Drawable downloadImage(String _url)
        {
            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            //BufferedInputStream buf;
            try {
                url = new URL(_url);
                in = url.openStream();

                // Read the inputstream
                buf = new BufferedInputStream(in);

                // Convert the BufferedInputStream to a Bitmap
                Bitmap bMap = BitmapFactory.decodeStream(buf);

                //Drawable d = new BitmapDrawable(getResources(), bMap);


                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }

                return new BitmapDrawable(bMap);

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }
    }*/

    /*private void setImageTab(Drawable drawable) {

        setBackgroundDrawableTab(drawable);
    }*/

    /*public void setBackgroundDrawableTab(Drawable drawable) {

        //logocol.setBackgroundDrawable(drawable);

        TabLayout.Tab t = tabLayout.newTab();
        t.setCustomView(R.layout.item_tabs);
        ImageView image = (ImageView)t.getCustomView().findViewById(R.id.img_persona);
        CardView card = (CardView)t.getCustomView().findViewById(R.id.card_notificacion);

        if (notification_count >= 0)  {
            card.setVisibility(View.VISIBLE);
        }else{
            card.setVisibility(View.GONE);
        }
        image.setImageDrawable(drawable);
        tabLayout.addTab(t);

        //tabLayout.addTab(tabLayout.newTab().setIcon(drawable));
    }*/



    private void saveFieldEntradas(FieldsEntradas fieldsEntrada) {
        fieldsEntradas.put(String.valueOf(fieldsEntrada.getIndice()), fieldsEntrada);

    }

    public List<FieldsEntradas> getfieldsEntradas() {
        return new ArrayList<>(fieldsEntradas.values());
    }



}
