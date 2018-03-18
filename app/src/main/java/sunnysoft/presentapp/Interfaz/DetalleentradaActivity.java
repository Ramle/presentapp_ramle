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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import sunnysoft.presentapp.Interfaz.adapter.FieldsEventoAdapter;
import sunnysoft.presentapp.Interfaz.pojo.FieldsEvento;
import sunnysoft.presentapp.R;
import android.support.v7.widget.Toolbar;

public class DetalleentradaActivity extends AppCompatActivity {

    private DatabaseHelper midb;
    String url;
    String urlT;
    TextView nombreevento;
    TextView cursogrupo;
    TextView txv_nombre;
    TextView txv_fecha;
    TextView fechainicioevento;
    TextView horainicioevento;
    TextView dataresponsables;
    ImageView img_persona;
    String email;
    final ProgressDialog[] progressDialog = new ProgressDialog[1];
    List<String>nomesImage = new ArrayList<>();
    int notification_count;
    private Toolbar secundaria;

    String token;
    String subdomain;
    private HashMap<String, FieldsEvento> fieldsEventos = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalleentrada);

        //Tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getText(R.string.txt_menu_entradas));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle datos = getIntent().getExtras();
        if (datos != null){
            url = datos.getString("DetailUrl");
            urlT = datos.getString("tURL");
        }
        midb = new DatabaseHelper(this);

        Cursor Resultados = midb.Session();


        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));

        url += "?token="+token;
        url += "&email="+ email;

        //date format

        final ListView mfieldsEventosList;
        final DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        final DateFormat originalFormatHour = new SimpleDateFormat("HH:mm:ss");

        final TagGroup mTagGroup = (TagGroup) findViewById(R.id.tag_group);
        mfieldsEventosList = (ListView) findViewById(R.id.fieldeventos_list);

        //traer datos de ws
        final AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;

        nombreevento = (TextView) findViewById(R.id.nombreevento);
        cursogrupo = (TextView) findViewById(R.id.cursogrupo);
        txv_nombre = (TextView) findViewById(R.id.txv_nombre);
        txv_fecha = (TextView) findViewById(R.id.txv_fecha);
        fechainicioevento = (TextView) findViewById(R.id.fechainicioevento);
        horainicioevento = (TextView) findViewById(R.id.horainicioevento);
        dataresponsables = (TextView) findViewById(R.id.dataresponsables);

        img_persona = (ImageView) findViewById(R.id.img_persona);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab3);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(DetalleentradaActivity.this, MenuActivity.class);

                startActivity(i);

            }

        });*/

        // Invoke RESTful Web Service with Http parameters
        RequestHandle post = client.get(DetalleentradaActivity.this, url, entity, "application/json", new AsyncHttpResponseHandler() {

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
                    time_string = jsonobject.getString("time_string");
                    date_string = jsonobject.getString("date_string");
                    created_by_name  = jsonobject.getString("created_by_name");
                    tipo_persona  = jsonobject.getString("tipo_persona");
                    JSONArray jsonarray = new JSONArray(jsonobject.getString("tags"));
                    JSONArray jsonarray_field = new JSONArray(jsonobject.getString("fields"));

                    Date daterefinit = originalFormat.parse(created_at);
                    String dateref = originalFormat.format(daterefinit);
                    String hourref = originalFormatHour.format(daterefinit);
                    //Toast.makeText(DetalleentradaActivity.this, "Fallo por "+dateref, Toast.LENGTH_LONG).show();
                    //Toast.makeText(DetalleentradaActivity.this, "Fallo por "+hourref, Toast.LENGTH_LONG).show();

                    nombreevento.setText(proceso_name);
                    cursogrupo.setText(curso_grupo);
                    txv_nombre.setText(user_name);
                    fechainicioevento.setText(date_string);
                    horainicioevento.setText(time_string);
                    dataresponsables.setText(created_by_name);
                    nombreevento.setTextColor(Color.parseColor("#DC9233"));
                    txv_fecha.setText(tipo_persona);
                    //descarga de imagen logo del colegio
                    new DetalleentradaActivity.DownloadImage().execute(user_image);
                    String [] tags = new String [jsonarray.length()];
                    String [] fields = new String [jsonarray.length()];

                    for(int i=0; i < jsonarray.length(); i++) {

                        JSONObject jsonobject_tags = jsonarray.getJSONObject(i);
                        name       = jsonobject_tags.getString("name");
                        tags[i] = name;

                        // Toast.makeText(DetalleentradaActivity.this, "Fallo por "+name, Toast.LENGTH_LONG).show();
                    }

                    mTagGroup.setTags(tags);
                    int k = 1;
                    int cantidad = 0;


                    for(int j=0; j < jsonarray_field.length(); j++) {

                        final FieldsEventoAdapter mfieldsEventoAdapter;

                        JSONObject jsonobject_fields = jsonarray_field.getJSONObject(j);
                        nameField       = jsonobject_fields.getString("name");
                        contentField       = jsonobject_fields.getString("content");
                        //   FieldsEvento fieldsEvento = new FieldsEvento(nameField, contentField, j + 1);

                        saveFieldEventos(new FieldsEvento(nameField, contentField, j + 1));
                        //  Toast.makeText(DetalleentradaActivity.this, "Fallo por "+fieldsEvento.getIndice(), Toast.LENGTH_LONG).show();
                        // Toast.makeText(DetalleentradaActivity.this, "Fallo por "+fieldsEvento.getDetalle(), Toast.LENGTH_LONG).show();

                        // Inicializar el adaptador con la fuente de datos.
                        mfieldsEventoAdapter = new FieldsEventoAdapter(DetalleentradaActivity.this,getFieldEventos());

                        //Relacionando la lista con el adaptador
                        mfieldsEventosList.setAdapter(mfieldsEventoAdapter);
                        k += j;

                    }


                    if(cantidad > 500){

                        mfieldsEventosList.getLayoutParams().height= 500;

                    }else{

                        if(cantidad > 250 || k > 1){

                            mfieldsEventosList.getLayoutParams().height= 350;

                        }
                    }

                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                    Toast.makeText(DetalleentradaActivity.this, "Fallo por a", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DetalleentradaActivity.this, "Fallo por "+e, Toast.LENGTH_LONG).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                if (statusCode == 404) {
                    Log.i("On Failure", "404");
                    Toast.makeText(DetalleentradaActivity.this, "Fallo por 404 ", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Log.i("On Failure", "500");
                    Toast.makeText(DetalleentradaActivity.this, "Fallo por 500 ", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(DetalleentradaActivity.this, "Fallo por NN ", Toast.LENGTH_LONG).show();
                }

            }

        });


        //Tooblar

        secundaria = (Toolbar) findViewById(R.id.toolbar_secundaria);
        secundaria.setNavigationIcon(R.drawable.arrow_back);
        TextView titulo_secundaria = (TextView) secundaria.findViewById(R.id.toolbar_secundaria_title);
        titulo_secundaria.setText("Ver Entrada");
        secundaria.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent i = new Intent(DetalleentradaActivity.this, VerEntradas.class);
                onBackPressed();
                //startActivity(i);
            }
        });


        /*Toolbar toolbarfecha = (Toolbar) findViewById(R.id.toolbarfecha);
        TextView toolbar_titlefecha = (TextView)toolbarfecha.findViewById(R.id.toolbar_titlefecha);
        setSupportActionBar(toolbarfecha);
        toolbar_titlefecha.setText("Ver Entrada");
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/

    }

    // funciones de descarga de imagenes
    private void setImage(Drawable drawable) {

        setBackgroundDrawable(drawable);
    }

    @Deprecated
    public void setBackgroundDrawable(Drawable drawable) {

        img_persona.setBackgroundDrawable(drawable);
    }

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


    private void saveFieldEventos(FieldsEvento fieldsEvento) {
        fieldsEventos.put(String.valueOf(fieldsEvento.getIndice()), fieldsEvento);
    }

    public List<FieldsEvento> getFieldEventos() {
        return new ArrayList<>(fieldsEventos.values());
    }

}
