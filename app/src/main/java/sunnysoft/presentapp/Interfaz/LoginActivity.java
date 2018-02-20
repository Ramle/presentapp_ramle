package sunnysoft.presentapp.Interfaz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.regex.Matcher;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.R;

public class LoginActivity extends AppCompatActivity {

    // Declaracion de componentes
    private Button login;
    public EditText email;
    public EditText password;
    public TextView olvidopwd;
    private ImageView logocol;
    // Declaracion de variables
    String correo;
    String pwd;
    String logo;
    String subdomain;
    String forgot_password_url;
    private Bitmap loadedImage;
    private String imageHttpAddress;
    // variable de base de datos
    private DatabaseHelper midb;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        // Inicia base de datos
        midb = new DatabaseHelper(this);
        //recibe datos de activity valcolegio
        Bundle datos = getIntent().getExtras();
        logo = datos.getString("logo");
        subdomain = datos.getString("subdomain");
        forgot_password_url = datos.getString("forgot_password_url");
        // se setea variable de manejo de imagen
        imageHttpAddress = logo;
        //iniciar componentes
        olvidopwd = (TextView) findViewById(R.id.olvidopwd);
        login = (Button) findViewById(R.id.button);
        email = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);

        //funciones de botones
        recordar(forgot_password_url);


        // eliminar bd
        //midb.logouth();
        //midb.oncreateusers();

        //descarga de imagen logo del colegio
        new DownloadImage().execute(imageHttpAddress);
        logocol = (ImageView) findViewById(R.id.logocol);

        context = this;

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCampos();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(LoginActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }
// funciones de descarga de imagenes
    private void setImage(Drawable drawable) {

        setBackgroundDrawable(drawable);
    }

    @Deprecated
    public void setBackgroundDrawable(Drawable drawable) {

        logocol.setBackgroundDrawable(drawable);
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




    private void validarCampos(){
        if (email.getText().toString().equals("") || password.getText().toString().equals("")){
            Toast.makeText(context, "Los campos no pueden estra vacios", Toast.LENGTH_SHORT).show();
        }else{
            if (validarCorreo(email.getText().toString())){
                login();
            }
        }
    }

    // funcion que realiza la autenticacion de el usuario

    private void login(){

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;

//declaracion de variables locales
        correo = email.getText().toString();
        pwd = password.getText().toString();
        String url = "http://"+subdomain;
        url += ".present.com.co/api/login?email=";
        url += "" + correo + "&password=";
        url += ""+pwd+"";

// llamado del servicio
        RequestHandle post  = client.post(url, new AsyncHttpResponseHandler() {

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
                String user_name =null;
                String user_type = null;
                String user_image = null;
                String token = null;



                try {

//respuesta del servicio
                    responseStr = new String(responseBody, "UTF-8");
// manejo del primer nivel de objetos
                    JSONObject user = new JSONObject(responseStr);
// Se obtiene valores del objeto
                    String valorLlave = user.getString("user");
// manejo del segundo nivel de objetos
                    JSONObject user2 = new JSONObject(valorLlave);

// Se obtiene valores del objeto
                    user_name = user2.getString("user_name");
                    user_type = user2.getString("user_type");
                    user_image = user2.getString("user_image");
                    token = user2.getString("token");


//Registro de variables de session en base de datos
                    midb.registrarlogin(correo,user_name,user_image,token,logo, user_type , subdomain);

                    progressDialog[0].dismiss();



// se llama a la activity menu y se envia parametros
                    Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                    i.putExtra("user_name", user_name);
                    i.putExtra("user_image",user_image);
                    i.putExtra("user_type",user_type);
                    i.putExtra("token",token);
                    i.putExtra("logo", logo);
                    i.putExtra("subdomain", subdomain);
                    i.putExtra("email", correo);
                    startActivity(i);


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                if (statusCode == 422) {
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
                        String msgerror = errorxa.getString("Login");
// se maneja el array json
                        JSONArray jsonarray = new JSONArray(msgerror);

                        progressDialog[0].dismiss();

//se obtiene cada uno de los mensajes que se encuentran dentro del json
                        for(int i=0; i < jsonarray.length(); i++) {
                            String mensaje = jsonarray.getString(i);
                            Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
// When Http response code is '500'
                else if (statusCode == 500) {

                    progressDialog[0].dismiss();
                    Toast.makeText(LoginActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();

                }
// When Http response code other than 404, 500
                else {
//Log.i("On Failure", "NN");
                    progressDialog[0].dismiss();
                    Toast.makeText(LoginActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

//Institución no valida.
                }


            }

        });



    }
// funcion que realiza el llamado del webview de recordar contraseña
    private void recordar(final String forgot_password_url){

        olvidopwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(LoginActivity.this, forgotActivity.class);
                i.putExtra("forgot_password_url", forgot_password_url);
                startActivity(i);

            }
        });





    }


    private boolean validarCorreo(String correo){
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(correo);
        if (matcher.matches()){
            return true;
        }else{
            Toast.makeText(this, "Ingrese un Correo Valido", Toast.LENGTH_SHORT).show();;
            return false;

        }
    }


}


