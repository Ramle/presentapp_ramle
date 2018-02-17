package sunnysoft.presentapp.Interfaz;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import cz.msebera.android.httpclient.entity.StringEntity;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Interfaz.adapter.CorreosAdapter;
import sunnysoft.presentapp.Interfaz.adapter.CorreosDetalleAdapter;
import sunnysoft.presentapp.Interfaz.pojo.CorreoDetalle;
import sunnysoft.presentapp.R;

public class DetallemailActivity extends AppCompatActivity {



    private Toolbar secundaria;
    private String URL;
    private String urlservice;

    private String subdomain;
    private String token;
    private String email;

    private DatabaseHelper midb;
    Context context;
    private RecyclerView recycler_detalle_correo;
    private CorreosDetalleAdapter adapter;
    private List<CorreoDetalle> correoDetalles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detallemail);

        secundaria = (Toolbar) findViewById(R.id.toolbar);
        secundaria.setNavigationIcon(R.drawable.arrow_back);
        TextView titulo_secundaria = (TextView) secundaria.findViewById(R.id.toolbar_secundaria_title);
        titulo_secundaria.setText("Detalle Correo");
        secundaria.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            URL = extras.getString("servicio");
        }

        midb = new DatabaseHelper(this);
        context = this;
        Cursor Resultados = midb.Session();
        subdomain =Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));

        urlservice = URL;
        urlservice += "?token="+token;
        urlservice += "&email="+ email;

        recycler_detalle_correo = (RecyclerView)findViewById(R.id.recycler_correo_detalles);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_detalle_correo.setLayoutManager(linearLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;

        RequestHandle post = client.get(urlservice, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                try {
                    //respuesta del servicio
                    responseStr = new String(responseBody, "UTF-8");
                    // manejo del primer nivel de objetos
                    JSONObject principal = new JSONObject(responseStr);

                    String valorLlave = principal.getString("email");

                    JSONObject detalle_correo = new JSONObject(valorLlave);

                    String mensajes = detalle_correo.getString("messages");
                    JSONArray correos = new JSONArray(mensajes);

                    for (int a=0;a<correos.length();a++){
                        String itemPrim = correos.getString(a);

                        JSONObject valores = new JSONObject(itemPrim);

                        String body = valores.getString("body");
                        String date = valores.getString("date");
                        String time = valores.getString("time");
                        String user_image = valores.getString("user_image");
                        String name = valores.getString("name");
                        String images = valores.getString("images");
                        String files = valores.getString("files");

                        JSONArray items = new JSONArray(images);
                        JSONArray items2 = new JSONArray(files);

                        List<String> name_files = new ArrayList<>();
                        List<String> url_files = new ArrayList<>();
                        List<String> urls_images = new ArrayList<>();

                        for (int i=0;i<items.length();i++){
                            String item = items.getString(i);
                            JSONObject valoresFiles = new JSONObject(item);
                            name_files.add(valoresFiles.getString("original_name"));
                            url_files.add(valoresFiles.getString("url"));
                        }

                        for (int j=0;j<items2.length();j++){
                            String item = items.getString(j);
                            JSONObject valoresImages = new JSONObject(item);
                            urls_images.add(valoresImages.getString("url"));
                        }

                        correoDetalles.add(new CorreoDetalle(name,user_image,body,date,time,name_files,url_files,urls_images));
                    }

                    adapter = new CorreosDetalleAdapter(getApplicationContext(), correoDetalles);
                    recycler_detalle_correo.setAdapter(adapter);



                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }catch (JSONException e){
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
                            Toast.makeText(DetallemailActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }

                        //midb.logouth();
                        //midb.oncreateusers();
                        //Intent i = new Intent(MuralesActivity.this, InicioActivity.class);
                        //startActivity(i);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // When Http response code is '500'
                else if (statusCode == 500) {

                    Toast.makeText(DetallemailActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(DetallemailActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

                    //Institución no valida.
                }

            }
        });
    }

}
