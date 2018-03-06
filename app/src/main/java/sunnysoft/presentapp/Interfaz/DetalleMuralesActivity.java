package sunnysoft.presentapp.Interfaz;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Interfaz.pojo.Murales;
import sunnysoft.presentapp.R;

public class DetalleMuralesActivity extends AppCompatActivity {

    private GridView grid, grid_adjuntos;

    // declaracion de BD
    private DatabaseHelper midb;

    Context context;

    private WebView webContenido;
    private TextView txvFecha;
    private TextView txvNombre;
    private ImageView imgPersona;
    ArrayList<String> url = new ArrayList<>();

    String urlservice;

    String subdomain;
    String token;
    String email;

    private String user_photo;
    private String user_name;
    private String created_at;
    private String content;

    private Toolbar secundaria;

    //private ConstraintLayout cont_adjuntos;

    // imagenes y botones

    LinearLayout detallemurales;
    Button btnadjunto;
    ImageView imagenes;

    @Override
    public void onBackPressed() {
        Toast.makeText(DetalleMuralesActivity .this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_murales);

        detallemurales = (LinearLayout) findViewById(R.id.detallemural);


        webContenido = (WebView)findViewById( R.id.web_contenido );

        txvFecha = (TextView)findViewById( R.id.txv_fecha );
        txvNombre = (TextView)findViewById( R.id.txv_nombre );
        imgPersona = (ImageView)findViewById( R.id.img_persona );
        //cont_adjuntos = (ConstraintLayout)findViewById(R.id.cont_adjuntos);
        //grid = (GridView) findViewById(R.id.grid_galeria);

        midb = new DatabaseHelper(this);
        context = this;

        Cursor Resultados = midb.Session();

        subdomain =Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));

        secundaria = (Toolbar) findViewById(R.id.toolbar_secundaria);
        secundaria.setNavigationIcon(R.drawable.arrow_back);
        TextView titulo_secundaria = (TextView) secundaria.findViewById(R.id.toolbar_secundaria_title);
        titulo_secundaria.setText("Detalle Comunicado");
        secundaria.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetalleMuralesActivity.this, MuralesActivity.class);
                startActivity(i);
            }
        });



        Bundle datos = getIntent().getExtras();
        urlservice = datos.getString("servicio");
        urlservice += "?token="+token;
        urlservice += "&email="+ email;

        //Log.e("url", urlservice);



        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;

        RequestHandle post  = client.get(urlservice, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                // Declaracion de variables
                String responseStr = null;


                try {
                    //respuesta del servicio
                    responseStr = new String(responseBody, "UTF-8");
                    // manejo del primer nivel de objetos
                    JSONObject principal = new JSONObject(responseStr);

                    String valorLlave = principal.getString("comunicado");

                    JSONObject comunicados = new JSONObject(valorLlave);

                    String id = comunicados.getString("id");
                    user_photo = comunicados.getString("user_photo");
                    user_name = comunicados.getString("user_name");
                    created_at = comunicados.getString("created_at");
                    content = comunicados.getString("content");
                    String photos = comunicados.getString("photos");
                    String files = comunicados.getString("files");

                    JSONArray items = new JSONArray(photos);
                    JSONArray items2 = new JSONArray(files);

                    //HashMap<String,String> archivos2 = new HashMap<>();
                    //HashMap<String,String> photos2 = new HashMap<>();
                    List<String> archivos = new ArrayList<>();
                    List<String> urlarchivos = new ArrayList<>();

                    List<String> photos3 = new ArrayList<>();
                    List<String> urlphotos3 = new ArrayList<>();


                    for (int i = 0; i < items.length(); i++) {

                        String item = items.getString(i);

                        JSONObject valores = new JSONObject(item);

                        photos3.add(valores.getString("original_name"));
                        urlphotos3.add(valores.getString("url"));


                    }

                    for (int a = 0; a < items2.length(); a++) {

                        String item2 = items2.getString(a);

                        JSONObject valores2 = new JSONObject(item2);

                        archivos.add(valores2.getString("original_name"));
                        urlarchivos.add(valores2.getString("url"));


                    }

                    Picasso.with(context).load(user_photo).error(R.drawable.logo).into(imgPersona);
                    txvNombre.setText(user_name);
                    txvFecha.setText(created_at);
                    webContenido.getSettings().setJavaScriptEnabled(true);
                    webContenido.loadDataWithBaseURL(null,content,"text/html","utf-8",null);

                    construiradjuntos(archivos, urlarchivos);
                    construirphotos(photos3, urlphotos3);


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
                            Toast.makeText(DetalleMuralesActivity.this, mensaje, Toast.LENGTH_LONG).show();
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

                    Toast.makeText(DetalleMuralesActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(DetalleMuralesActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

                    //Institución no valida.
                }

            }
        });


    }

    public void construiradjuntos(List<String> archivos, final List<String> urlarchivos){

        for (int a = 0 ; a < archivos.size(); a++){

            btnadjunto = new Button(this);
            btnadjunto.setBackgroundResource(R.drawable.botones_secundarios);
            btnadjunto.setTextColor(getResources().getColor(R.color.color_letra_btn_prim));


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(10,10,10,10);

            btnadjunto.setText(archivos.get(a));

            final int finalA = a;
            btnadjunto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(urlarchivos.get(finalA)));
                    context.startActivity(i);

                }
            });
            btnadjunto.setLayoutParams(params);
            detallemurales.addView(btnadjunto);

        }

    }


    public void construirphotos(List<String> photos3, final List<String> urlphotos3){


        for (int a = 0 ; a < photos3.size(); a++){

            imagenes = new ImageView(this);
            //imagenes.setScaleType(ImageView.ScaleType.FIT_CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(5,10,5,10);


            imagenes.setLayoutParams(params);


            Picasso.with(context).load(urlphotos3.get(a)).error(R.drawable.logo).into(imagenes);
            imagenes.setOnClickListener(new DetalleMuralesActivity.OnImageClickListener(a, urlphotos3));

            detallemurales.addView(imagenes);

        }

    }

    class OnImageClickListener implements View.OnClickListener {

        int _position;
        List<String> _urls = new ArrayList<>();

        public OnImageClickListener(int _position, List<String> urllist) {
            this._position = _position;
            this._urls = urllist;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, GaleriaDetalleActivity.class);
            i.putExtra("position",_position);
            i.putStringArrayListExtra("urls", (ArrayList<String>) _urls);
            context.startActivity(i);

        }
    }



}

