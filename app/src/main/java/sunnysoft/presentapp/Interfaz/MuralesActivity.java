package sunnysoft.presentapp.Interfaz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Interfaz.adapter.MuralesAdapter;
import sunnysoft.presentapp.Interfaz.pojo.Murales;
import sunnysoft.presentapp.R;
import sunnysoft.presentapp.utils.EndlessRecyclerViewScrollListener;

public class MuralesActivity extends AppCompatActivity {

    // Definir variables


    public String token;

    String subdomain;
    public String email;
    String url;
    String nombremural;
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
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    Context context;

    final ProgressDialog[] progressDialog = new ProgressDialog[1];

    @Override
    public void onBackPressed() {
        Toast.makeText(MuralesActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_murales);

        // Recibir parametros

        midb = new DatabaseHelper(this);
        context = this;

        Cursor Resultados = midb.Session();

        subdomain =Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));

        // construir url de servicio de tabs

        url = "http://"+subdomain;
        url += ".present.com.co//api/murales/names";
        url += "?token="+token;
        url += "&email="+ email;

        urls = new ArrayList<>();
        nomes = new ArrayList<>();


        // funcion que crea los tabs
        //
        seteartabs(url);

        //Tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getText(R.string.txt_menu_murales));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

/*
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);



        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MuralesActivity.this, MenuActivity.class);
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
                    String valorLlave = user.getString("murales");

                    JSONArray items = new JSONArray(valorLlave);

                    for(int i=0; i < items.length(); i++) {
                        String item = items.getString(i);

                        JSONObject valores = new JSONObject(item);

                        nombremural = valores.getString("name");
                        notification_count = valores.getInt("notificaciones_count");
                        mural_url = valores.getString("mural_url");

                        Log.e("mural", mural_url);

                        urls.add(mural_url);
                        nomes.add(nombremural);

                    }

                    for (int a = 0; a< nomes.size(); a++){

                        /////////////////////creacion de tabs dinamicamente//////////////////////
                        tabLayout.addTab(tabLayout.newTab().setText(nomes.get(a)));
                    }

                    progressDialog[0].dismiss();


                    // Create the adapter that will return a fragment for each of the three
                    // primary sections of the activity.
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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
                            Toast.makeText(MuralesActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }

                        midb.logouth();
                        midb.oncreateusers();
                        progressDialog[0].dismiss();
                        Intent i = new Intent(MuralesActivity.this, InicioActivity.class);
                        startActivity(i);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // When Http response code is '500'
                else if (statusCode == 500) {

                    Toast.makeText(MuralesActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(MuralesActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

                    //Institución no valida.
                }


            }

        });


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        View rootView;

        private static final String ARG_SECTION_NUMBER = "section_number";

        List<Murales> muralesList;
        MuralesAdapter adapter;
        RecyclerView recyclerMurales;
        public int contador;


        public PlaceholderFragment() {


        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, List<String> urls, String email, String token) {
            PlaceholderFragment fragment = new PlaceholderFragment();
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
            rootView = inflater.inflate(R.layout.fragment_murales, container, false);
            recyclerMurales = (RecyclerView) rootView.findViewById(R.id.recycler_murales);
            muralesList = new ArrayList<>();
            List<String> urls_fragment = new ArrayList<>();

            Bundle extras = getArguments();
            final int contador = extras.getInt("position");
            urls = extras.getStringArrayList("ARG_urls");
            final String email= extras.getString("email");
            final String token= extras.getString("token");

            String url = urls.get(contador-1);
            url += "?token="+token;
            url += "&email="+ email;
            //Log.e("Data: ", "Data contador " + urls.size());

            try{

                // GetEntradasData getEntradasData = new GetEntradasData(url);
                ExecuteTask executeTask = new ExecuteTask();
                executeTask.execute(url);

                JSONObject JsonDataMurales = new JSONObject(executeTask.get());
                //Log.i("Goood", "onCreateView : "+JsonDataEntrada);
                // JSONObject dataEntradas = getEntradasData.getResponse();

                muralesList = Contenido(contador,JsonDataMurales);

                // Inicializar el adaptador con la fuente de datos.
                adapter = new MuralesAdapter(getContext(), muralesList);
                recyclerMurales.setAdapter(adapter);

            }catch (Exception e){

                Log.e("Exception", "onCreateView: "+ e);

            }

            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            //Log.i("", "onCreateViewList: "+ muralesList.get(0).getTotal());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerMurales.setLayoutManager(linearLayoutManager);

            EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                    String url = urls.get(contador-1);
                    url += "&token="+token;
                    url += "&email="+ email;
                    Log.e("Data: ", "Data contador " + linearLayoutManager.getItemCount());
                    Log.e("Data: ", "Data contador " + url);
                    // Toast.makeText(getContext(), linearLayoutManager.getItemCount(), Toast.LENGTH_LONG).show();

                    if (!url.equals("null"+"&token="+token+"&email="+ email)){


                        ExecuteTask executeTask = new ExecuteTask();
                        executeTask.execute(url);

                        JSONObject JsonDataEntrada = null;
                        try {
                            JsonDataEntrada = new JSONObject(executeTask.get());
                            //Log.i("", "onLoadMore Data good: "+JsonDataEntrada );
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("", "onLoadMore JSONException: "+e );
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e("", "onLoadMore InterruptedException: "+e );
                        } catch (ExecutionException e) {
                            Log.e("", "onLoadMore ExecutionException: "+e );
                            e.printStackTrace();
                        }
                        //Log.i("Goood", "onCreateView : "+JsonDataEntrada);
                        // JSONObject dataEntradas = getEntradasData.getResponse();

                        final int curSize = adapter.getItemCount();
                        List<Murales> MuralesMoreList = Contenido(contador,JsonDataEntrada);
                        // adapter.notifyItemInserted(EntradasList.size() - 1);


                        muralesList.addAll(MuralesMoreList);
                        Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                adapter.notifyItemRangeInserted(curSize,muralesList.size() -1);
                            }
                        };

                        handler.post(r);

                    }


                }

            };

            recyclerMurales.addOnScrollListener(scrollListener);
            // mocking network delay for API call

            // fin scroll view

            return rootView;
        }

        public List<Murales> Contenido(final int contador, JSONObject responseMurales){


            List<Murales> MuralesList_Contenido = new ArrayList<>();


            // llamado del servicio

            // Declaracion de variables
            String responseStr = null;
            Boolean isfiles = false;
            Boolean isphotos = false;

            try {

                // Se obtiene valores del objeto
                String valorLlave = responseMurales.getString("comunicados");

                JSONObject segundoobj = new JSONObject(valorLlave);

                String urlnextpage = segundoobj.getString("next_page_url");

                String total = segundoobj.getString("total");
                String to = segundoobj.getString("to");

                String valordata = segundoobj.getString("data");

                JSONArray items = new JSONArray(valordata);

                // Toast.makeText(getContext(), urlnextpage, Toast.LENGTH_LONG).show();

                for (int i = 0; i < items.length(); i++) {

                    String item = items.getString(i);

                    JSONObject valores = new JSONObject(item);

                    String user_namedata = valores.getString("user_name");
                    String created_atdata = valores.getString("created_at");
                    String contentdata = valores.getString("content");
                    String user_photo = valores.getString("user_photo");
                    String read_more = valores.getString("read_more");
                    String url_detalle = valores.getString("detail_url");

                    String file = valores.getString("files");
                    String phoos = valores.getString("photos");
                    //String read_more = "false";

                    HashMap<String,String> archivos2 = new HashMap<>();
                    HashMap<String,String> photos2 = new HashMap<>();

                    JSONArray fle = new JSONArray(file);
                    for (int a = 0; a < fle.length(); a++) {
                        String fles = fle.getString(a);

                        JSONObject its = new JSONObject(fles);

                        //archivos.add(new files(its.getString("original_name"), its.getString("url")));
                        archivos2.put(its.getString("original_name"),its.getString("url"));

                    }

                    JSONArray fle2 = new JSONArray(phoos);
                    for (int b = 0; b < fle2.length(); b++) {
                        String fles2 = fle2.getString(b);
                        JSONObject its2 = new JSONObject(fles2);
                        photos2.put(its2.getString("original_name"),its2.getString("url"));

                    }

                    if(archivos2.isEmpty()){
                        isfiles = false;
                    }  else {
                        isfiles = true;
                    }

                    if(photos2.isEmpty()){
                        isphotos = false;
                    }  else {
                        isphotos = true;

                    }

                    MuralesList_Contenido.add(new Murales(user_namedata, created_atdata, contentdata,user_photo, read_more, isfiles, isphotos, archivos2, photos2, url_detalle));

                    archivos2 = null;
                    photos2 = null;

                }

                urls.set(contador-1, urlnextpage);

                // Log.e("Data: ", "Data Url " + urls.get(contador - 1));

            }   catch (JSONException e) {
                e.printStackTrace();
            }


            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            //return rootView;
            //return rootView;
            return MuralesList_Contenido;

        }

        class ExecuteTask extends AsyncTask<String, Integer, String>
        {

            @Override
            protected String doInBackground(String... params) {

                String res=PostData(params);
                //  Log.i("", "doInBackground: "+res);

                return res;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
            }

        }

        public String PostData(String[] valuse) {
            String s="";
            try
            {
                HttpClient httpClient=new DefaultHttpClient();
                HttpGet httpGet=new HttpGet(valuse[0]);

                HttpResponse httpResponse=  httpClient.execute(httpGet);

                HttpEntity httpEntity=httpResponse.getEntity();

                s= readResponse(httpResponse);

            }
            catch(Exception exception)  {}
            return s;

        }

        public String readResponse(HttpResponse res) {
            InputStream is=null;
            String return_text="";
            try {
                is=res.getEntity().getContent();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
                String line="";
                StringBuffer sb=new StringBuffer();
                while ((line=bufferedReader.readLine())!=null)
                {
                    sb.append(line);
                }
                return_text=sb.toString();
            } catch (Exception e)
            {

            }

            return return_text;

        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, urls , email, token);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return urls.size();
        }
    }
}
