package sunnysoft.presentapp.Interfaz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.R;

// clase de validacion de colegios

public class valcolegiosActivity extends AppCompatActivity {

    private Button Validar;
    public EditText codigo;
    private String valcodigo;
    String url = "http://serverprueba.present.com.co/api/validate/institution?institution_code=";
    private DatabaseHelper midb;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valcolegios);

        context = this;

        // Inicia base de datos
        midb = new DatabaseHelper(this);

        //inicia componentes
        Validar = (Button)findViewById(R.id.btn_validar);
        codigo = (EditText)findViewById(R.id.edtx_cod_colegio);

        // funciones de reseteo
         //midb.logouth();
        //midb.oncreateusers();

        //valida si hay sesion activa
        validarsession();
        // accion del boton
        Validarinstitucion();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(valcolegiosActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }

    private void validarsession(){

        // se realiza consulta en bd
        Cursor validacion = midb.Session();

        // se valida que existan resultados y se llama activity menu

        if (validacion.moveToFirst()) {

            String username =validacion.getString(validacion.getColumnIndex("user_name"));
            String user_image =validacion.getString(validacion.getColumnIndex("user_image"));
            String token =validacion.getString(validacion.getColumnIndex("token"));
            String logo =validacion.getString(validacion.getColumnIndex("logo"));
            String user_type =validacion.getString(validacion.getColumnIndex("usertype"));
            String subdomain =validacion.getString(validacion.getColumnIndex("subdomain"));
            String correo =validacion.getString(validacion.getColumnIndex("user"));

            Intent i = new Intent(valcolegiosActivity.this, MenuActivity.class);
            i.putExtra("user_name", username);
            i.putExtra("user_image",user_image);
            i.putExtra("token",token);
            i.putExtra("logo", logo);
            i.putExtra("user_type", user_type);
            i.putExtra("subdomain", subdomain);
            i.putExtra("email", correo);
            startActivity(i);
        }

    }

    // funcion de actividad del boton

    private void Validarinstitucion(){
        Validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncHttpClient client = new AsyncHttpClient();
                JSONObject jsonParams = new JSONObject();
                valcodigo = codigo.getText().toString();
                valcodigo = valcodigo.replace(" ","");
                 url += ""+valcodigo+"";
                final ProgressDialog[] progressDialog = new ProgressDialog[1];


                // llama  RESTful Web Service con parametros http
                RequestHandle post = client.post(url, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart(){

                        super.onStart();
                        progressDialog[0] = ProgressDialog.show(
                                context, "Por favor espere", "Procesando...");

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        // declaracion de variables a usar
                        String responseStr = null;
                        String subdomain = null;
                        String logo = null;
                        String institution_code = null;
                        String forgot_password_url = null;

                        try {
                            // se obtiene respuesta del servicio
                            responseStr = new String(responseBody, "UTF-8");

                            // se maneja los objetos del primer nivel del json
                            JSONObject institution = new JSONObject(responseStr);
                            // se obtiene contenido del objetp
                            String valorLlave = institution.getString("institution");
                            // se maneja los objetos del segundo nivel del json
                            JSONObject institution2 = new JSONObject(valorLlave);
                            // se obtiene los objetos del segundo nivel del json
                            subdomain = institution2.getString("subdomain");
                            logo = institution2.getString("logo");
                            forgot_password_url = institution2.getString("forgot_password_url");

                            progressDialog[0].dismiss();

                            // se llama la activity menu y se envian parametros
                            Intent i = new Intent(valcolegiosActivity.this, LoginActivity.class);
                            i.putExtra("subdomain", subdomain);
                            i.putExtra("logo",logo);
                            i.putExtra("forgot_password_url",forgot_password_url);
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

                            String responseStr = null;

                            try {
                                responseStr = new String(responseBody, "UTF-8");
                                JSONObject errorx = new JSONObject(responseStr);
                                String valorLlave = errorx.getString("errors");
                                JSONObject errorxa = new JSONObject(valorLlave);
                                String msgerror = errorxa.getString("institution_code");
                                JSONArray jsonarray = new JSONArray(msgerror);

                                progressDialog[0].dismiss();

                                for(int i=0; i < jsonarray.length(); i++) {
                                    String mensaje = jsonarray.getString(i);

                                    Toast.makeText(valcolegiosActivity.this, "El código de la institución es requerido", Toast.LENGTH_LONG).show();
                                }

                                progressDialog[0].dismiss();

                                Intent i = new Intent(valcolegiosActivity.this, valcolegiosActivity.class);
                                 startActivity(i);

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {

                            Toast.makeText(valcolegiosActivity.this, "Error status code 500 ", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {

                            Toast.makeText(valcolegiosActivity.this, "On Failure ", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });

    }
}
