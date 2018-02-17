package sunnysoft.presentapp.Interfaz;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;
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

public class CreareventoActivity extends AppCompatActivity implements View.OnClickListener {

    EditText fechacomienzo;
    EditText fechafin;
    EditText nombreevento;
    String selectedDate;
    String selectedDatefin;
    HttpPost httppost;
    String post_url;
    String urlv;
    Button enviodataevento;
    CheckBox checkbox;
    private DatabaseHelper midb;
    String email;
    String token;
    @Override
    public void onBackPressed() {
        Toast.makeText(CreareventoActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crearevento);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(CreareventoActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });

        midb = new DatabaseHelper(this);

        Cursor Resultados = midb.Session();
        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));

        //TextView

        fechacomienzo = (EditText) findViewById(R.id.fechacomienzo);
        fechacomienzo.setOnClickListener(this);

        fechafin = (EditText) findViewById(R.id.fechafin);
        fechafin.setOnClickListener(this);

        nombreevento = (EditText) findViewById(R.id.nombreevento);

        enviodataevento = (Button) findViewById(R.id.enviodataevento);
        enviodataevento.setOnClickListener(this);

        checkbox = (CheckBox) findViewById(R.id.checkBox);
        checkbox.setOnClickListener(this);

        //Tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText("CALENDARIO");
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //Tooblar
        Toolbar toolbarfecha = (Toolbar) findViewById(R.id.toolbarfecha);
        TextView toolbar_titlefecha = (TextView)toolbarfecha.findViewById(R.id.toolbar_titlefecha);
        setSupportActionBar(toolbarfecha);
        toolbar_titlefecha.setText("Crear evento");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fechacomienzo:
                showDatePickerDialog();
                break;
            case R.id.fechafin:
                showDatePickerDialog_fin();
                break;
            case R.id.checkBox:
                showDatePickerDialogTodoDia();
                break;
            case R.id.enviodataevento:
                Parsearjson();
                String proceso = "Crear Evento";
                String nombre = null;
                new MyAsyncTask(CreareventoActivity.this, httppost, proceso, urlv, nombre)
                        .execute();
                break;
        }

    }

    private void showDatePickerDialog() {
        FragmentManager fm = getFragmentManager();

        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                selectedDate = twoDigits(year) + "-" + twoDigits(month+1) + "-" + twoDigits(day);
                //fechacomienzo.setText(selectedDate);
                showTimePickerDialog_time();
            }
        });
        newFragment.show(fm, "datePicker");


    }

    private void showDatePickerDialog_fin() {
        FragmentManager fm = getFragmentManager();

        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                selectedDatefin = twoDigits(year) + "-" + twoDigits(month+1) + "-" + twoDigits(day);
                //fechafin.setText(selectedDate);
                showTimePickerDialog_timefin();
            }
        });
        newFragment.show(fm, "datePicker");

    }

    private void showDatePickerDialogTodoDia() {
        FragmentManager fm = getFragmentManager();

        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                selectedDate = twoDigits(year) + "-" + twoDigits(month+1) + "-" + twoDigits(day);
                selectedDatefin = twoDigits(year) + "-" + twoDigits(month+1) + "-" + twoDigits(day + 1);
                fechacomienzo.setText(selectedDate + " " + "00" + ":" + "00" + ":00");
                fechafin.setText(selectedDatefin + " " + "00" + ":" + "00" + ":00");
                //fechacomienzo.setText(selectedDate);
            }
        });
        newFragment.show(fm, "datePicker");


    }

    private void showTimePickerDialog_time() {
        FragmentManager fm = getFragmentManager();

        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                fechacomienzo.setText(selectedDate + " " + String.valueOf(twoDigits(hourOfDay)) + ":" + String.valueOf(twoDigits(minute)) + ":00");
            }
        });
        newFragment.show(fm, "datePicker");

    }

    private void showTimePickerDialog_timefin() {
        FragmentManager fm = getFragmentManager();

        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                fechafin.setText(selectedDatefin + " " + String.valueOf(twoDigits(hourOfDay)) + ":" + String.valueOf(twoDigits(minute)) + ":00");
            }
        });
        newFragment.show(fm, "datePicker");

    }

    public void Parsearjson() {

        post_url = "http://serverprueba.present.com.co/api/calendar/store";
        //   post_url += "?token=$2y$10$CUR8/fvGJKaIFUESg5its.snr0DdZkAl3YcPIpbGtgrNa94caWgta";
        //  post_url += "&email=admin@dc.co";

        httppost = new HttpPost(post_url);
        httppost.addHeader("Content-Type", "application/json");
        try {
            //forma el JSON y tipo de contenido

            /*JSONObject title = new JSONObject(String.valueOf(nombreevento.getText()));
            JSONObject start = new JSONObject(String.valueOf(fechacomienzo.getText()));
            JSONObject end = new JSONObject(String.valueOf(fechafin.getText()));
            JSONObject ad = new JSONObject(String.valueOf(true));*/
            JSONObject j = new JSONObject();
            //j.put("key","users_ids");
            j.put("title",String.valueOf(nombreevento.getText()));

            j.put("start",String.valueOf(fechacomienzo.getText()));
            j.put("end",String.valueOf(fechafin.getText()));


            j.put("ad",true);
            j.put("token",token);
            j.put("email",email);
            Log.d("On real"+j.toString(), "NN");

            StringEntity stringEntity = new StringEntity( j.toString());
            //  Toast.makeText(CreareventoActivity.this, j.toString(), Toast.LENGTH_LONG).show();

            stringEntity.setContentType( (Header) new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(stringEntity);

        } catch (JSONException e) {
            Toast.makeText(CreareventoActivity.this, "Error"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(CreareventoActivity.this, "Error"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(CreareventoActivity.this, "Error"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

}
