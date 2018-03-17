package sunnysoft.presentapp.Interfaz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Interfaz.adapter.AdapterMenu;
import sunnysoft.presentapp.R;
import sunnysoft.presentapp.firebase.MyFirebaseInstanceIDService;

public class MenuActivity extends AppCompatActivity {

    // Declaracion de variables

    String user_name;
    String user_image;
    String token;
    String logo;
    String user_type;
    String subdomain;
    String email;
    String url;

    String nombremenu;
    String nombremenu2;
    int notification_count;
    MyFirebaseInstanceIDService MF;

    // declaracion de BD
    private DatabaseHelper midb;

    //Declaracion de componentes

    ImageView menlogocol;
    ImageView menimageuser;
    CardView cardviewuser;

    Context context;


    TextView lbl_nombre;
    TextView lbl_profecion;


    // gridview

    GridView gv;

    ArrayList<String> prgmNameList = new ArrayList<String>();
    ArrayList<String> prgmDisplayList = new ArrayList<String>();
    ArrayList<Integer> prgmImages = new ArrayList<Integer>();
    ArrayList<Integer> prgnotificacion = new ArrayList<Integer>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // recibe datos de otra activity



        Bundle datos = getIntent().getExtras();


        // Inicia base de datos
        midb = new DatabaseHelper(this);


        context = this;

        Cursor Resultados = midb.Session();

        subdomain =Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));
        logo =Resultados.getString(Resultados.getColumnIndex("logo"));
        user_image =Resultados.getString(Resultados.getColumnIndex("user_image"));

        //Toast.makeText(MenuActivity.this, " "+ getResources().getDisplayMetrics().densityDpi, Toast.LENGTH_LONG).show();

        //Iniciar de componentes

        menlogocol = (ImageView) findViewById(R.id.menlogocol);
        menimageuser = (ImageView) findViewById(R.id.menimageuser);
        cardviewuser = (CardView) findViewById(R.id.cardView);

        if(getResources().getDisplayMetrics().densityDpi <= DisplayMetrics.DENSITY_HIGH){

            menlogocol.getLayoutParams().width = 190;
            menlogocol.getLayoutParams().height = 95;
            menlogocol.setAdjustViewBounds(true);

            menlogocol.setTop(150);

            menimageuser.getLayoutParams().width = 140;
            menimageuser.getLayoutParams().height = 140;
            cardviewuser.getLayoutParams().width = 140;
            cardviewuser.getLayoutParams().height = 140;
            cardviewuser.setRadius(70);
            menimageuser.setAdjustViewBounds(true);

        }

        lbl_nombre = (TextView) findViewById(R.id.lbl_nombre);
        lbl_profecion = (TextView) findViewById(R.id.lbl_profecion);


        // setear url del servicio

        url = "http://"+subdomain;
        url += ".present.com.co/api/menu";
        url += "?token="+token;
        url += "&email="+ email;

        Log.e("url", url);
        // setear imagenes
        new MenuActivity.DownloadImage().execute(logo);
       // new MenuActivity.DownloadImage2().execute(user_object.getString("user_image"));

        // setear textos principales

        //lbl_nombre.setText(user_name);
        //lbl_profecion.setText(user_type);

        // iniciar gridview
        gv=(GridView) findViewById(R.id.gridmenu);

        //setear menu
        crearmenu(url);

        MF = new MyFirebaseInstanceIDService(MenuActivity.this);
        MF.onTokenRefresh();

        sendRegistrationToServer();

    }
    @Override

    public void onBackPressed() {
        Toast.makeText(MenuActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }

    private void crearmenu(String url){

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;



        // llamado del servicio
        RequestHandle post  = client.get(url, new AsyncHttpResponseHandler() {

            final ProgressDialog[] progressDialog = new ProgressDialog[1];


            @Override
            public void onStart(){

                super.onStart();
                progressDialog[0] = ProgressDialog.show(
                        context, "Por favor espere", "Procesando...");

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
                    JSONObject user_object = new JSONObject(user.getString("user"));
                    // Se user_name valores del objeto

                   /* String valorLlave2 = user.getString("user");

                    JSONObject user2 = new JSONObject(valorLlave2);
                    //Log.e("user" user2)
                    // setear textos principales

                       lbl_nombre.setText(user2.getString("user_name"));
                       lbl_profecion.setText(user2.getString("user_type"));*/

                    String valorLlave = user.getString("menu");
                    lbl_nombre.setText(user_object.getString("user_name"));
                    lbl_profecion.setText(user_object.getString("user_type") );

                    new MenuActivity.DownloadImage2().execute(user_object.getString("user_image"));

                    JSONArray items = new JSONArray(valorLlave);

                    for(int i=0; i < items.length(); i++) {
                        String item = items.getString(i);

                        JSONObject valores = new JSONObject(item);

                        nombremenu = valores.getString("name");
                        nombremenu2 = valores.getString("display_name");
                        notification_count = valores.getInt("notification_count");

                        prgmNameList.add(nombremenu);
                        prgmDisplayList.add(nombremenu2);
                        prgnotificacion.add(notification_count);

                        switch (nombremenu){

                            case "murales":
                                prgmImages.add(R.drawable.murales);
                                break;
                            case "modulos":
                                prgmImages.add(R.drawable.modulos);
                                break;
                            case "entradas":
                                prgmImages.add(R.drawable.entradas);
                                break;
                            case "email":
                                prgmImages.add(R.drawable.email);
                                break;
                            case "calendario":
                                prgmImages.add(R.drawable.calendario);
                                break;
                        }

                    }



                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("Mensaje error:",""+e);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Mensaje error:",""+e);
                }

                prgmNameList.add("Logouth");
                prgmDisplayList.add("Logouth");
                prgnotificacion.add(0);
                prgmImages.add(R.drawable.cerrarsession);

                //Log.e("imagenes", String.valueOf(prgmImages));

                progressDialog[0].dismiss();

                AdapterMenu adapter = new AdapterMenu((MenuActivity) context, prgmNameList,prgmImages, prgmDisplayList, prgnotificacion);
                gv.setAdapter(adapter);

                //gv.setAdapter(new AdapterMenu(getContext(), prgmNameList,prgmImages));


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
                            Toast.makeText(MenuActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }

                        midb.logouth();
                        midb.oncreateusers();

                        progressDialog[0].dismiss();
                        Intent i = new Intent(MenuActivity.this, InicioActivity.class);
                        startActivity(i);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // When Http response code is '500'
                else if (statusCode == 500) {

                    Toast.makeText(MenuActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(MenuActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

                    //Institución no valida.
                }


            }

        });
    }



    // funciones de descarga de imagenes
    private void setImage(Drawable drawable) {

        setBackgroundDrawable(drawable);
    }

    @Deprecated
    public void setBackgroundDrawable(Drawable drawable) {

        menlogocol.setBackgroundDrawable(drawable);
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


    // funciones de descarga de imagenes
    private void setImage2(Drawable drawable) {

        setBackgroundDrawable2(drawable);
    }

    @Deprecated
    public void setBackgroundDrawable2(Drawable drawable) {

        //extraemos el drawable en un bitmap
        //Drawable originalDrawable = getResources().getDrawable();
        Drawable originalDrawable = drawable;
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        //ImageView imageView = (ImageView) findViewById(R.id.imageView);

        //imageView.setImageDrawable(roundedDrawable);

        menimageuser.setBackgroundDrawable(roundedDrawable);
    }

    public class DownloadImage2 extends AsyncTask<String, Integer, Drawable> {
        @Override
        protected Drawable doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0]);
        }

        protected void onPostExecute(Drawable image)
        {
            setImage2(image);
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

    public void sendRegistrationToServer(){
        RequestParams params = new RequestParams();
        // se realiza consulta en bd
        //Cursor validacion = mImpuestosClientesDbHelper.Session();
        Cursor token_firebase = midb.Gettoken();
        String iduser = null;
        String refreshedToken = null;

        /*if (validacion.moveToFirst()) {

            iduser = validacion.getString(validacion.getColumnIndex("id"));

        }*/

        if (token_firebase.moveToFirst()) {

            refreshedToken = token_firebase.getString(token_firebase.getColumnIndex("token"));

        }
        //iduser = "5";
        params.put("token_firebase", refreshedToken);
        String urlpost = "http://"+subdomain;
        urlpost += ".present.com.co/api/notificacion/token/store";
        urlpost += "?token="+token;
        urlpost += "&email="+ email;
        Log.i("", "sendRegistrationToServer: "+urlpost);

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(urlpost, params, new AsyncHttpResponseHandler() {

            final ProgressDialog[] progressDialog = new ProgressDialog[1];

            @Override
            public void onStart() {
                super.onStart();
                progressDialog[0] = ProgressDialog.show(
                        MenuActivity.this, "Por favor espere", "Procesando...");
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                System.out.println("statusCode "+statusCode);//statusCode 200
                //Toast.makeText(MenuActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();
                //Log.e("onFailure: ", "Bienvenido");
                progressDialog[0].dismiss();

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog[0].dismiss();
                Log.e("onFailure: ", "Error al enviar Token, por favor comunicarlo al equipo de ");
                Toast.makeText(MenuActivity.this, "Error al enviar Token, por favor comunicarlo al equipo de ", Toast.LENGTH_LONG).show();

            }
        });
    }



}
