package sunnysoft.presentapp.Interfaz;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import sunnysoft.presentapp.Interfaz.adapter.Nivel_Uno_Adapter;
import sunnysoft.presentapp.Interfaz.pojo.Nivel_Dos;
import sunnysoft.presentapp.Interfaz.pojo.Nivel_uno;
import sunnysoft.presentapp.R;

public class ModulosActivity extends AppCompatActivity {

    private DatabaseHelper midb;

    String url;
    String token;
    String email;
    String subdomain;

    Context context;


    RecyclerView recycler_nivel_uno;
    List<Nivel_uno> lista = new ArrayList<>();
    List<Nivel_Dos> lista2 = new ArrayList<>();

    //Context context;

    @Override
    public void onBackPressed() {
        Toast.makeText(ModulosActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulos);

        //Tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getText(R.string.txt_menu_modulos));
        toolbar_title.setTextColor(getResources().getColor(R.color.color_letra_in_prim));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        midb = new DatabaseHelper(this);

        context = this;

        // llamado de datos de base de datos

        Cursor Resultados = midb.Session();

        subdomain =Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));


        // setear url de servicio

        url = "http://"+subdomain;
        url += ".present.com.co/api/modulos/entradas";
        url += "?token="+token;
        url += "&email="+ email;

        setearmodulos(url);
        recycler_nivel_uno = (RecyclerView)findViewById(R.id.recycler_primer_nivel);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);





        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ModulosActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });



    }


    private void setearmodulos(String url){

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;

        // llamado del servicio
        RequestHandle post  = client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                // Declaracion de variables
                String responseStr = null;
                String nombremodulo;
                String nombreproceso;
                String view_all_url;
                String nombrecurso;
                String urlcurso;


                try {

                    //respuesta del servicio
                    responseStr = new String(responseBody, "UTF-8");
                    // manejo del primer nivel de objetos
                    JSONObject user = new JSONObject(responseStr);
                    // Se obtiene valores del objeto
                    String valorLlave = user.getString("modulos");

                    JSONArray items = new JSONArray(valorLlave);

                    for(int i=0; i < items.length(); i++) {
                        String item = items.getString(i);

                        JSONObject valores = new JSONObject(item);
                        nombremodulo = valores.getString("nombre");
                        lista.add(new Nivel_uno(nombremodulo) );
                        String llave2 = valores.getString("procesos");
                        lista2.add(new Nivel_Dos(llave2));

                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Nivel_Uno_Adapter adapter;
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recycler_nivel_uno.setLayoutManager(linearLayoutManager);



                adapter = new Nivel_Uno_Adapter(context, lista, lista2);
                recycler_nivel_uno.setAdapter(adapter);
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
                            Toast.makeText(ModulosActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }
                        midb.logouth();
                        midb.oncreateusers();
                        Intent i = new Intent(ModulosActivity.this, InicioActivity.class);
                        startActivity(i);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(ModulosActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(ModulosActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

                }


            }

        });
    }
}
