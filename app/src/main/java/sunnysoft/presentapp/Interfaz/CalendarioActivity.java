package sunnysoft.presentapp.Interfaz;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.entity.StringEntity;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Interfaz.adapter.EventosAdapter;
import sunnysoft.presentapp.Interfaz.pojo.Eventos;
import sunnysoft.presentapp.R;

public class CalendarioActivity extends AppCompatActivity {

    // Definir variables

    private DatabaseHelper midb;
    String user_name;
    String user_image;
    String token;
    String logo;
    String user_type;
    String subdomain;
    String email;
    String url;
    Locale spanish = new Locale("es", "ES");
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", spanish);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "CalendarioFragment";
    //Cursor resultado;

    //private BDExample BDExample;
    CompactCalendarView compactCalendar;
    TextView CalendarMonth;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM | yyyy", spanish);
    private SimpleDateFormat dateFormatMonthTitle = new SimpleDateFormat("MMMM", spanish);
    private SimpleDateFormat dateFormatDayTitle = new SimpleDateFormat("dd", spanish);

    String fechaImpuesto;
    private HashMap<String, Eventos> eventos = new HashMap<>();
    private HashMap<String, Eventos> eventosDia = new HashMap<>();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        midb = new DatabaseHelper(this);

        Cursor Resultados = midb.Session();

        subdomain =Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));

        final ListView mEventoList;

        mEventoList = (ListView) findViewById(R.id.eventodia_list);
        // Recibir parametros

        //  Bundle datos = getIntent().getExtras();
        //token = datos.getString("token");
        user_type = ".present.com.co/api/calendar";

        final Date dateactual = new Date();
        String fechaactual = dateFormatMonth.format(dateactual);

        // Instancia de helper

        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        final DateFormat originalFormatref = new SimpleDateFormat("yyyy-MM-dd");
        final DateFormat targetFormatref = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //Tooblar
        Toolbar toolbarfecha = (Toolbar) findViewById(R.id.toolbarfecha);
        final TextView toolbar_titlefecha = (TextView)toolbarfecha.findViewById(R.id.toolbar_titlefecha);
        setSupportActionBar(toolbarfecha);
        toolbar_titlefecha.setText(ucFirst(fechaactual));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final TextView refdia = (TextView) findViewById(R.id.refdia);

        //Tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText("CALENDARIO");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        try{

            // Creamos un nuevo Bundle
            Bundle args = new Bundle();

            // Colocamos el String
            args.putString("subdomain", subdomain);
            args.putString("user_type", user_type);
            args.putString("token", token);
            args.putString("email", email);

            // Supongamos que tu Fragment se llama TestFragment. Colocamos este nuevo Bundle como argumento en el fragmento.

        }catch(Exception e){

            Toast.makeText(CalendarioActivity.this, "error"+e, Toast.LENGTH_SHORT).show();

        }

        //Calendario

        // CalendarMonth = (TextView)findViewById(R.id.CalendarMonth);
        Event ev1 = null;

        final ArrayList<String> EventosArrayList = new ArrayList<String>();
        final ArrayList<ArrayList> EventosArrayListArray = new ArrayList<ArrayList>();
        // Añade el elemento al ArrayList

        //traer datos de ws
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;


        //url = "http://"+subdomain;
        url = "http://"+subdomain;
        url += user_type;
        url += "?token="+token;
        url += "&email="+ email;
        //url += "?token="+token;
        //url += "&email="+ email;

        // Invoke RESTful Web Service with Http parameters
        RequestHandle post = client.get(CalendarioActivity.this, url, entity, "application/json", new AsyncHttpResponseHandler() {

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
                String title = null;
                String start = null;
                String end = null;
                String ad = null;
                String tipo_evento = null;
                String detail_url = null;
                String date_string = null;
                String time_string = null;

                try {

                    responseStr = new String(responseBody, "UTF-8");
                    JSONArray jsonarray = new JSONArray(responseStr);
                    for(int i=0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        id       = jsonobject.getString("id");
                        title       = jsonobject.getString("title");
                        start       = jsonobject.getString("start");
                        end       = jsonobject.getString("end");
                        ad       = jsonobject.getString("ad");
                        tipo_evento       = jsonobject.getString("tipo_evento");
                        date_string       = jsonobject.getString("date_string");
                        time_string       = jsonobject.getString("time_string");

                        try {
                            detail_url = jsonobject.getString("detail_url");
                        }catch (Exception e){
                            detail_url = null;
                        }

                        try {

                            try {

                                try {
                                    fechaImpuesto = start;

                                    DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat originalFormatHour = new SimpleDateFormat("HH:mm:ss");
                                    DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date = originalFormat.parse(fechaImpuesto);
                                    String dateref = originalFormat.format(date);
                                    String hourref = originalFormatHour.format(date);
                                    // String dateref = new SimpleDateFormat("yyyy-MM-dd").format(fechaImpuesto);
                                    //test
                                    //i += 1;

                                    saveEvento(new Eventos(id, title, tipo_evento, dateref,  time_string, detail_url, i + 1,date_string));
                                    ev1 = new Event(Color.parseColor("#69B4E8") , date.getTime(),start);
                                    //Set an event for Teachers' Professional Day 2016 which is 21st of October
                                    compactCalendar.addEvent(ev1);
                                } catch(Exception e) {

                                    Toast.makeText(CalendarioActivity.this, "Fine"+e, Toast.LENGTH_SHORT).show();
                                    // dateFormat.parse() could throw a ParseException
                                }
                            } catch(Exception e) {

                                Toast.makeText(CalendarioActivity.this, "error"+e, Toast.LENGTH_LONG).show();


                            }
                        }catch (Exception e){
                            //Toast.makeText(Activity_main.this, "Fallo por "+e, Toast.LENGTH_LONG).show();
                            Log.i("WSUsuarios","Fallo por "+e);
                        }
                    }

                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                    Toast.makeText(CalendarioActivity.this, "Fallo por a", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CalendarioActivity.this, "Fallo por "+e, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                if (statusCode == 404) {
                    Log.i("On Failure", "404");
                    Toast.makeText(CalendarioActivity.this, "Fallo por 404 ", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Log.i("On Failure", "500");
                    Toast.makeText(CalendarioActivity.this, "Fallo por 500 ", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(CalendarioActivity.this, "Fallo por NN ", Toast.LENGTH_LONG).show();
                }

            }
        });

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();

                try{

                    //Toast.makeText(CalendarioActivity.this, "error"+dateClicked, Toast.LENGTH_SHORT).show();

                    String dayclicked = dateFormatDayTitle.format(dateClicked);
                    String monthclicked = dateFormatMonthTitle.format(dateClicked);
                    String dateyearclicked = originalFormatref.format(dateClicked);

                    long diff = dateClicked.getTime() - dateactual.getTime();
                    long segundos = diff / 1000;
                    long minutos = segundos / 60;
                    long horas = minutos / 60;
                    long dias = horas / 24;
                    final EventosAdapter mEventoAdapter;

                    if (dias >= 0 && dateClicked != dateactual){
                        refdia.setText("Dentro de " + (dias + 1) + " | días "+dayclicked + " de " + ucFirst(monthclicked));
                    }else{
                        refdia.setText(dayclicked + " de " + ucFirst(monthclicked));
                    }

                    for(Iterator<Map.Entry<String, Eventos>> it = eventos.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<String, Eventos> entry = it.next();

                        Eventos value = entry.getValue();
                        String key = entry.getKey();

                        //entry.getKey().equals("test")

                        if(value.getFechaeevento().equals(dateyearclicked)) {

                            saveEvento_Dia(value);

                        }
                    }

                    // Inicializar el adaptador con la fuente de datos.
                    mEventoAdapter = new EventosAdapter(CalendarioActivity.this,getEventosDia(),dateyearclicked);

                    //Relacionando la lista con el adaptador
                    mEventoList.setAdapter(mEventoAdapter);

                    // Eventos
                    mEventoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Bundle datos = getIntent().getExtras();
                            Eventos currentEvento = mEventoAdapter.getItem(position);

                            if(currentEvento.getDetail_url() != null){

                                String getDetail_url = currentEvento.getDetail_url();
                                Intent i = new Intent(CalendarioActivity.this, VereventoActivity.class);
                                i.putExtra("DetailUrl", getDetail_url);
                                startActivity(i);

                            }

                        }
                    });

                    for(Iterator<Map.Entry<String, Eventos>> it = eventosDia.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<String, Eventos> entry = it.next();

                       Eventos value = entry.getValue();
                       String key = entry.getKey();

                        Log.e("error","error: "+value.getFechaeevento());
                        Log.e("error","error: "+dateyearclicked);
                        it.remove();

                        //entry.getKey().equals("test")
                    }

                    //}

                }catch(Exception e){

                    Toast.makeText(CalendarioActivity.this, "error"+e, Toast.LENGTH_SHORT).show();

                    Log.e("error","error: "+e);

                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                try{

                    toolbar_titlefecha.setText(ucFirst(dateFormatMonth.format(firstDayOfNewMonth)));
                    // CalendarMonth.setText(dateFormatMonth.format(firstDayOfNewMonth));
                }catch(Exception e){
                    Toast.makeText(CalendarioActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //fin calendario

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CalendarioActivity.this, MenuActivity.class);
                i.putExtra("user_name", user_name);
                i.putExtra("user_image",user_image);
                i.putExtra("user_type",user_type);
                i.putExtra("token",token);
                i.putExtra("logo", logo);
                i.putExtra("subdomain", subdomain);
                i.putExtra("email", email);
                startActivity(i);
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CalendarioActivity.this, CreareventoActivity.class);
                i.putExtra("user_name", user_name);
                i.putExtra("user_image",user_image);
                i.putExtra("user_type",user_type);
                i.putExtra("token",token);
                i.putExtra("logo", logo);
                i.putExtra("subdomain", subdomain);
                i.putExtra("email", email);
                startActivity(i);
            }
        });


    }

    private void saveEvento(Eventos evento) {
        eventos.put(String.valueOf(evento.getIndice()), evento);
    }


    private void saveEvento_Dia(Eventos evento_dia) {
        eventosDia.put(String.valueOf(evento_dia.getIndice()), evento_dia);
    }

    public List<Eventos> getEventos() {
        return new ArrayList<>(eventos.values());
    }
    public List<Eventos> getEventosDia() {
        return new ArrayList<>(eventosDia.values());
    }

    public static String ucFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

}