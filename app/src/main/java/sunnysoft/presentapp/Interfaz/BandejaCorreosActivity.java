
package sunnysoft.presentapp.Interfaz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Interfaz.adapter.CorreosAdapter;
import sunnysoft.presentapp.Interfaz.pojo.Correos;
import sunnysoft.presentapp.R;
import sunnysoft.presentapp.utils.EndlessRecyclerViewScrollListener;

public class BandejaCorreosActivity extends AppCompatActivity {

    // Definir variables


    public String token;

    String subdomain;
    public String email;
    String url;
    String nombreMenuCorreo;
    int notification_count;
    String mural_url;


    public static List<String> urls;
    List<String> nomes;



    // declaracion de BD
    private DatabaseHelper midb;

    // tabs layouts

    TabLayout tabLayout;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private BandejaCorreosActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    Context context;

    final ProgressDialog[] progressDialog = new ProgressDialog[1];

    @Override
    public void onBackPressed() {
        Toast.makeText(BandejaCorreosActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandeja_correos);

        // Recibir parametros

        midb = new DatabaseHelper(this);
        context = this;

        Cursor Resultados = midb.Session();

        subdomain = Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token = Resultados.getString(Resultados.getColumnIndex("token"));
        email = Resultados.getString(Resultados.getColumnIndex("user"));

        // construir url de servicio de tabs

        url = "http://" + subdomain;
        url += ".present.com.co//api/email/menu";
        url += "?token=" + token;
        url += "&email=" + email;

        urls = new ArrayList<>();
        nomes = new ArrayList<>();


        // funcion que crea los tabs
        //
        seteartabs(url);
        Log.i("URL",url);

        //Tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getText(R.string.txt_email));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton redactar = (FloatingActionButton) findViewById(R.id.redactar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(BandejaCorreosActivity.this, MenuActivity.class);
                startActivity(i);
            }
        });

        redactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(BandejaCorreosActivity.this, RedactaremailActivity.class);
                startActivity(i);
            }
        });

    }

    public void seteartabs(final String url){

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        // llamado del servicio
        RequestHandle post  = client.get(url, new AsyncHttpResponseHandler() {



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
                    // Se obtiene valores del objeto
                    String valorLlave = user.getString("menu");

                    JSONArray items = new JSONArray(valorLlave);

                    for(int i=0; i < items.length(); i++) {
                        String item = items.getString(i);

                        JSONObject valores = new JSONObject(item);

                        nombreMenuCorreo = valores.getString("name");
                        notification_count = valores.getInt("notificaciones_count");
                        mural_url = valores.getString("mural_url");

                        Log.e("correo menu", mural_url);

                        urls.add(mural_url);
                        nomes.add(nombreMenuCorreo);

                    }

                    for (int a = 0; a< nomes.size(); a++){

                        /////////////////////creacion de tabs dinamicamente//////////////////////
                        tabLayout.addTab(tabLayout.newTab().setText(nomes.get(a)));
                    }

                    progressDialog[0].dismiss();


                    // Create the adapter that will return a fragment for each of the three
                    // primary sections of the activity.
                    mSectionsPagerAdapter = new BandejaCorreosActivity.SectionsPagerAdapter(getSupportFragmentManager());

                    // Set up the ViewPager with the sections adapter.
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);


                    mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                    tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));



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
                            Toast.makeText(BandejaCorreosActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }

                        midb.logouth();
                        midb.oncreateusers();
                        Intent i = new Intent(BandejaCorreosActivity.this, InicioActivity.class);
                        startActivity(i);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // When Http response code is '500'
                else if (statusCode == 500) {

                    Toast.makeText(BandejaCorreosActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(BandejaCorreosActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

                    //Institución no valida.
                }


            }

        });
    }
    ////////////////////////////////////////

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        View rootView;

        private static final String ARG_SECTION_NUMBER = "section_number";

        List<Correos> muralesList;
        CorreosAdapter adapter;
        RecyclerView recyclerCorreos;
        public int contador;


        public PlaceholderFragment() {


        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static BandejaCorreosActivity.PlaceholderFragment newInstance(int sectionNumber, List<String> urls, String email, String token) {
            BandejaCorreosActivity.PlaceholderFragment fragment = new BandejaCorreosActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt("position",sectionNumber);
            args.putStringArrayList("ARG_urls", (ArrayList<String>) urls);
            args.putString("email", email);
            args.putString("token", token);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_correos, container, false);
            recyclerCorreos = (RecyclerView) rootView.findViewById(R.id.recycler_correos);
            muralesList = new ArrayList<>();
            List<String> urls_fragment = new ArrayList<>();

            Bundle extras = getArguments();
            final int contador = extras.getInt("position");
            urls = extras.getStringArrayList("ARG_urls");
            final String email= extras.getString("email");
            final String token= extras.getString("token");

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerCorreos.setLayoutManager(linearLayoutManager);

            String url = urls.get(contador-1);
            url += "?token="+token;
            url += "&email="+ email;
            Log.e("Data: ", "Data contador " + urls.size());

            Contenido(contador,url, email, token);

            EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                    final int curSize = adapter.getItemCount();

                    String url = urls.get(contador-1);
                    url += "&token="+token;
                    url += "&email="+ email;
                    //   Log.e("Data: ", "Data contador " + urls.size());

                    Contenido(contador,url, email, token);

                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemRangeInserted(curSize, muralesList.size() - 1);
                        }
                    });
                }
            };

            recyclerCorreos.addOnScrollListener(scrollListener);

            return rootView;
        }

        public void Contenido(final int contador, String urls_param, String email, String token){

            AsyncHttpClient client = new AsyncHttpClient();
            // llamado del servicio

            RequestHandle post  = client.get(urls_param, new AsyncHttpResponseHandler() {

                final ProgressDialog[] progressDialog = new ProgressDialog[1];

                @Override
                public void onStart(){

                    super.onStart();

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    // Declaracion de variables
                    String responseStr = null;
                    Boolean isfiles = false;
                    Boolean isphotos = false;

                    try {

                        //respuesta del servicio
                        responseStr = new String(responseBody, "UTF-8");
                        // manejo del primer nivel de objetos
                        JSONObject comunicados = new JSONObject(responseStr);
                        // Se obtiene valores del objeto
                        String valorLlave = comunicados.getString("emails");

                        JSONObject segundoobj = new JSONObject(valorLlave);

                        String urlnextpage = segundoobj.getString("next_page_url");

                        String valordata = segundoobj.getString("data");

                        JSONArray items = new JSONArray(valordata);


                        for (int i = 0; i < items.length(); i++) {

                            String item = items.getString(i);

                            JSONObject valores = new JSONObject(item);

                            String date = valores.getString("date");
                            String time = valores.getString("time");
                            String subject = valores.getString("subject");
                            String url_detail = valores.getString("url_email_detail");
                            String participants = valores.getString("participants");
                            String user_image = valores.getString("user_image");
                            String isread = valores.getString("is_read");



                            muralesList.add(new Correos(participants, date, time, user_image, subject,url_detail));



                            //muralesList.add(new Correos(user_namedata, created_atdata, hora, asunto,user_photo, url_detalle));
                            adapter = new CorreosAdapter(getContext(), muralesList);
                            recyclerCorreos.setAdapter(adapter);

                        }

                        urls.set(contador-1, urlnextpage);

                        // Log.e("Data: ", "Data Url " + urls.get(contador - 1));

                    }  catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter = new CorreosAdapter(getContext(),muralesList);
                    recyclerCorreos.setAdapter(adapter);


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
                                Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                            }



                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // When Http response code is '500'
                    else if (statusCode == 500) {

                        Toast.makeText(getContext(), "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code other than 404, 500
                    else {
                        Log.i("On Failure", "NN");
                        Toast.makeText(getContext(), "On Failure ", Toast.LENGTH_LONG).show();

                        //Institución no valida.
                    }


                }

            });

        }
    }



    /////////////////////////////////////

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return BandejaCorreosActivity.PlaceholderFragment.newInstance(position + 1, urls , email, token);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return urls.size();
        }
    }



}
