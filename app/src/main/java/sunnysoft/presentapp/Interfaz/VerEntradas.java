package sunnysoft.presentapp.Interfaz;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Interfaz.adapter.EventosAdapter;
import sunnysoft.presentapp.Interfaz.adapter.ProcesoEntradasAdapter;
import sunnysoft.presentapp.Interfaz.pojo.Entradas;
import sunnysoft.presentapp.Interfaz.pojo.Eventos;
import sunnysoft.presentapp.R;
import sunnysoft.presentapp.utils.EndlessRecyclerViewScrollListener;

public class VerEntradas extends AppCompatActivity {

    String user_name;
    String user_image;
    String token;
    String logo;
    String user_type;
    String subdomain;
    String email;
    String url;
    private DatabaseHelper midb;
    private Toolbar secundaria;


    RecyclerView recyclerEntradas;
    ProcesoEntradasAdapter mProcesoEntradasAdapter;
    List<Entradas> EntradasList;
    //private HashMap<String, Entradas> entradas = new HashMap<>();

    @Override
    public void onBackPressed() {
        Toast.makeText(VerEntradas.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_entradas);


      //  final ListView mProcesoEntradasList;
        final String View_all_url = getIntent().getStringExtra("View_all_url");

        recyclerEntradas = (RecyclerView) findViewById(R.id.entradas_list);
      //  mProcesoEntradasList = (ListView) findViewById(R.id.entradas_list);

        midb = new DatabaseHelper(this);

        // llamado de datos de base de datos

        Cursor Resultados = midb.Session();

        subdomain =Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));


        url = View_all_url;
        url += "?token="+token;
        url += "&email="+ email;
        // Log.e("Data: ", "Data contador " + urls.size());

        //Tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getText(R.string.txt_menu_entradas));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(VerEntradas.this, MenuActivity.class);
                startActivity(i);
            }
        });



        try{

            // GetEntradasData getEntradasData = new GetEntradasData(url);
            ExecuteTask executeTask = new ExecuteTask();
            executeTask.execute(url);

            JSONObject JsonDataEntrada = new JSONObject(executeTask.get());
            // JSONObject dataEntradas = getEntradasData.getResponse();

            EntradasList = Contenido(JsonDataEntrada);
           // Log.i("Goood", "onCreateView : "+EntradasList);

            //Log.i("", "onCreateView: "+EntradasList);
            // Inicializar el adaptador con la fuente de datos.
            mProcesoEntradasAdapter = new ProcesoEntradasAdapter(getApplicationContext(),EntradasList);

            recyclerEntradas.setAdapter(mProcesoEntradasAdapter);

        }catch (Exception e){

            Log.e("Exception", "onCreateView: "+ e);

        }

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerEntradas.setLayoutManager(linearLayoutManager);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                String url_local = url;
                url_local += "&token="+token;
                url_local += "&email="+ email;
                Log.e("Data: ", "Data contador " + url_local);

               /* url = View_all_url;
                url += "?token="+token;
                url += "&email="+ email;*/
                // Log.e("Data: ", "Data contador " + urls.size());

                if (!url_local.equals("null"+"&token="+token+"&email="+ email)){

                    ExecuteTask executeTask = new ExecuteTask();
                    executeTask.execute(url_local);


                    JSONObject JsonDataEntrada = null;
                    try {
                        JsonDataEntrada = new JSONObject(executeTask.get());
                        Log.e("", "Ver: "+JsonDataEntrada );
                        //Log.i("", "onLoadMore Data good: "+JsonDataEntrada );
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("", "onLoadMore JSONException: "+e );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e("", "onLoadMore InterruptedException: "+e );
                    } catch (ExecutionException e) {
                        Log.e("", "onLoadMore ExecutionException: "+e );
                        e.printStackTrace();
                    }
                    //Log.i("Goood", "onCreateView : "+JsonDataEntrada);
                    // JSONObject dataEntradas = getEntradasData.getResponse();

                    // Inicializar el adaptador con la fuente de datos.

                    //Relacionando la lista con el adaptador

                    final int curSize = mProcesoEntradasAdapter.getItemCount();
                    List<Entradas> EntradasMoreList = Contenido(JsonDataEntrada);
                    // adapter.notifyItemInserted(EntradasList.size() - 1);
                    EntradasList.addAll(EntradasMoreList);
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            mProcesoEntradasAdapter.notifyItemRangeInserted(curSize,EntradasList.size() -1);
                        }
                    };

                    handler.post(r);

                }

            }
        };

       recyclerEntradas.addOnScrollListener(scrollListener);

    }

    public List<Entradas> Contenido(JSONObject responseEntradas){
        List<Entradas> ProcesoEntradasList_Contenido = new ArrayList<>();

        Event ev1 = null;
        // called when response HTTP status is "200 OK"
        String responseStr = null;
        String id = null;
        String user_name = null;
        String curso_grupo = null;
        String created_at = null;
        String detalles = null;
        String img_persona = null;
        String name = null;
        String urldetalle = null;

        try {

            JSONObject entradas = new JSONObject(responseEntradas.getString("entradas"));

            String urlnextpage = entradas.getString("next_page_url");
            JSONObject proceso = new JSONObject(responseEntradas.getString("proceso"));
            Log.e("", "Contenido: "+urlnextpage );

            String procesoname = proceso.getString("name");

            ///////////////////////segunda toolbar /////////////////////
            secundaria = (Toolbar) findViewById(R.id.toolbar_secundaria);
            secundaria.setNavigationIcon(R.drawable.arrow_back);
            TextView titulo_secundaria = (TextView) secundaria.findViewById(R.id.toolbar_secundaria_title);
            titulo_secundaria.setText(procesoname);
            secundaria.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(VerEntradas.this, ModulosActivity.class);
                    startActivity(i);
                }
            });

            JSONArray jsonarray = new JSONArray(entradas.getString("data"));
            JSONArray jsonarray_tags;

            for(int i=0; i < jsonarray.length(); i++) {

                JSONObject jsonobject_data = jsonarray.getJSONObject(i);

                id   = jsonobject_data.getString("id");
                user_name   = jsonobject_data.getString("user_name");
                curso_grupo = jsonobject_data.getString("curso_grupo");
                created_at  = jsonobject_data.getString("created_at");
                img_persona  = jsonobject_data.getString("user_image");
                detalles = curso_grupo + " " + created_at;
                jsonarray_tags = new JSONArray(jsonobject_data.getString("tags"));
                urldetalle  = jsonobject_data.getString("url_entrada_detail");

                String [] tags = new String [jsonarray_tags.length()];
                for(int j=0; j < jsonarray_tags.length(); j++) {

                    JSONObject jsonobject_tags = jsonarray_tags.getJSONObject(j);
                    name       = jsonobject_tags.getString("name");
                    //tags.add(name);
                    tags[j] = name;
                    //Toast.makeText(VerEntradas.this, "Bien por "+name, Toast.LENGTH_LONG).show();

                }

                try {

                    //entrada_1 = new Entradas(user_name, detalles, i + 1);

                  //  Log.e("Data: ", "Data Url " + detalles);
                    ProcesoEntradasList_Contenido.add(new Entradas(user_name, detalles, i + 1,img_persona,tags, urldetalle));
                    //saveEntrada(new Entradas(user_name, detalles, i + 1,img_persona,tags));

                    // Toast.makeText(VerEntradas.this, "Bien por "+entrada_1.getIndice(), Toast.LENGTH_LONG).show();
;

                }catch (Exception e){
                    Toast.makeText(VerEntradas.this, "Fallo por "+e, Toast.LENGTH_LONG).show();
                    Log.i("WSUsuarios","Fallo por "+e);
                }
            }

            url = urlnextpage;

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return ProcesoEntradasList_Contenido;
    }

    class ExecuteTask extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... params) {

            String res=PostData(params);
            //  Log.i("", "doInBackground: "+res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    public String PostData(String[] valuse) {
        String s="";
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpGet httpGet=new HttpGet(valuse[0]);

            HttpResponse httpResponse=  httpClient.execute(httpGet);

            HttpEntity httpEntity=httpResponse.getEntity();

            s= readResponse(httpResponse);

        }
        catch(Exception exception)  {}
        return s;

    }

    public String readResponse(HttpResponse res) {
        InputStream is=null;
        String return_text="";
        try {
            is=res.getEntity().getContent();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
            String line="";
            StringBuffer sb=new StringBuffer();
            while ((line=bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }
            return_text=sb.toString();
        } catch (Exception e)
        {

        }
        return return_text;

    }

}
