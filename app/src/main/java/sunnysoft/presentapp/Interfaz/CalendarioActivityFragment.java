package sunnysoft.presentapp.Interfaz;

import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Fragment;

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
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.entity.StringEntity;
import sunnysoft.presentapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
/**
 * A placeholder fragment containing a simple view.
 */
public class CalendarioActivityFragment extends Fragment {

    public CalendarioActivityFragment() {
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "CalendarioFragment";
    String url;
    //Cursor resultado;

    //private BDExample BDExample;
    CompactCalendarView compactCalendar;
    TextView CalendarMonth;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    String fechaImpuesto;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /*public CalendarioFragment() {
        // Required empty public constructor
    }*/


    public static CalendarioActivityFragment newInstance(){
        CalendarioActivityFragment f = new CalendarioActivityFragment();

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        Context context = getActivity();
        View root = inflater.inflate(R.layout.fragment_calendario, container, false);

        String subdomain = getArguments().getString("subdomain");
        String user_type = getArguments().getString("user_type");
        String token = getArguments().getString("token");
        String email = getArguments().getString("email");

        // recibe datos de otra activity

        //CalendarMonth = (TextView) root.findViewById(R.id.CalendarMonth);

        //   mImpuestosClientesDbHelper  = new ImpuestosClientesDbHelper(context);
        //   Cursor resultado = mImpuestosClientesDbHelper.getAllCalendario();
        Event ev1 = null;

        compactCalendar = (CompactCalendarView) root.findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        //Set an event for Teachers' Professional Day 2016 which is 21st of October

        //if (resultado.moveToFirst()){
        //  do {

//traer datos de ws
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;


        //url = "http://"+subdomain;
        url = subdomain;
        url += user_type;
        url += token;
        url += email;
        //url += "?token="+token;
        //url += "&email="+ email;

        // Invoke RESTful Web Service with Http parameters
        RequestHandle post = client.get(context, url, entity, "application/json", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                //mydb.borrar_Users();
                //mydb.oncreateusers();

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                Context context = getActivity();
                Event ev1 = null;
                // called when response HTTP status is "200 OK"
                String responseStr = null;
                String id = null;
                String title = null;
                String start = null;
                String end = null;
                String ad = null;
                String tipo_evento = null;
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

                        try {


                            try {

                                fechaImpuesto = start;
                                //fechaImpuesto = "2017-12-23 15:07:50";

                                //

                                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = originalFormat.parse(fechaImpuesto);
                                //String formattedDate = targetFormat.format(date);

                                ev1 = new Event(Color.BLUE, date.getTime(), "Test");
                                //Set an event for Teachers' Professional Day 2016 which is 21st of October
                                compactCalendar.addEvent(ev1);
                                //Toast.makeText(context, "ok"+ start, Toast.LENGTH_LONG).show();
                            } catch(Exception e) {

                                Toast.makeText(context, "error"+e, Toast.LENGTH_LONG).show();

                                // dateFormat.parse() could throw a ParseException
                                //    }

                                //} while (resultado.moveToNext());

                            }
                        }catch (Exception e){
                            //Toast.makeText(Activity_main.this, "Fallo por "+e, Toast.LENGTH_LONG).show();
                            Log.i("WSUsuarios","Fallo por "+e);
                        }
                    }

                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                    Toast.makeText(context, "Fallo por a", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Fallo por "+e, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                if (statusCode == 404) {
                    Log.i("On Failure", "404");;
                    Context context = getActivity();
                    Toast.makeText(context, "Fallo por 404 ", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Log.i("On Failure", "500");
                    Context context = getActivity();
                    Toast.makeText(context, "Fallo por 500 ", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Context context = getActivity();
                    Toast.makeText(context, "Fallo por NN ", Toast.LENGTH_LONG).show();
                }

            }




        });



        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getActivity();

                try{

                    //Aqui llamas el fragment que necesitas
                    //Creas el nuevo fragmento y una nueva transacción.
                    //Fragment nuevoFragmento = new ListItem();
                    //FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    /*transaction.replace(R.id.evento_frame, nuevoFragmento);
                    transaction.addToBackStack(null);
                    transaction.commit();
*/
                    // Toast.makeText(context, dateClicked.toString(), Toast.LENGTH_SHORT).show();

                }catch (Exception e) {

                    Toast.makeText(context, "No Events Planned for that day", Toast.LENGTH_SHORT).show();

                }

                // if (dateClicked.toString().compareTo("Fri Oct 21 00:00:00 AST 2016") == 0) {
                //     Toast.makeText(context, "Teachers' Professional Day", Toast.LENGTH_SHORT).show();
                //}else {
                //    Toast.makeText(context, "No Events Planned for that day", Toast.LENGTH_SHORT).show();
                //  }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                try{
                    //CalendarMonth.setText(dateFormatMonth.format(firstDayOfNewMonth));
                }catch(Exception e){

                    Context context = getActivity();
                    Toast.makeText(context, "Error" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;

    }

}