package sunnysoft.presentapp.Interfaz;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Datos.MyAsyncTask;
import sunnysoft.presentapp.R;

public class responder_emailActivity extends AppCompatActivity {

    String URL;
    EditText content;
    Button enviar;
    HttpPost httppost;
    private DatabaseHelper midb;
    Context context;
    String token;
    String email;
    String subdomain;
    private Toolbar secundaria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_email);


        midb = new DatabaseHelper(this);
        context = this;
        Cursor Resultados = midb.Session();
        subdomain = Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token = Resultados.getString(Resultados.getColumnIndex("token"));
        email = Resultados.getString(Resultados.getColumnIndex("user"));

        Bundle extras = getIntent().getExtras();

        URL = extras.getString("View_all_url");
        URL += "?token=" + token;
        URL += "&email=" + email;


        content =  (EditText) findViewById(R.id.editcontent);
         enviar = (Button) findViewById(R.id.btn_responderemail);

        //Tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getText(R.string.txt_responder_email));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        secundaria = (Toolbar) findViewById(R.id.toolbar_secundaria);
        secundaria.setNavigationIcon(R.drawable.arrow_back);
        TextView titulo_secundaria = (TextView) secundaria.findViewById(R.id.toolbar_secundaria_title);
        titulo_secundaria.setText("Detalle Email");

        secundaria.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        enviardatos();
    }
    public  void enviardatos(){

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nombre = null;

                if (Parsearjson()){

                    try {
                        String proceso = "responder Email";
                        //Log.e("httpost", String.valueOf(httppost));
                        new MyAsyncTask(responder_emailActivity.this, httppost, proceso, URL, nombre)
                                .execute();
                    }catch (Exception e){

                        Toast.makeText(responder_emailActivity.this, "Error"+e, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Los campos no deben estar vacios", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public Boolean Parsearjson() {

        //Log.e("url",URL);

        httppost = new HttpPost(URL);
        httppost.addHeader("Content-Type", "application/json");
        JSONObject j = new JSONObject();
        //j.put("key","users_ids");
        try {
            j.put("email", email);
            j.put("token", token);
            j.put("contents", content.getText());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(j.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringEntity.setContentType((Header) new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httppost.setEntity(stringEntity);

        return true;


    }
}
