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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import sunnysoft.presentapp.Interfaz.adapter.EntradasAdapter;

import sunnysoft.presentapp.Interfaz.pojo.Entradas;

import sunnysoft.presentapp.R;
import sunnysoft.presentapp.utils.EndlessRecyclerViewScrollListener;

public class EntradasActivity extends AppCompatActivity {

    // private static final int NUM_TABS = 4;

    private TabLayout tabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    String subdomain;
    String email;
    String url;
    String token;

    String image_user;
    String entradas_url;
    int Notification_count;
    List<String> nomesImage;

    public static List<String> urls;

    int notification_count;

    // declaracion de BD
    private DatabaseHelper midb;

    private ViewPager mViewPager;

    Context context;

    final ProgressDialog[] progressDialog = new ProgressDialog[1];

    @Override
    public void onBackPressed() {
        Toast.makeText(EntradasActivity.this, "El botón retroceder se ha deshabilitado", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entradas);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText("Entradas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        midb = new DatabaseHelper(this);

        Cursor Resultados = midb.Session();

        subdomain =Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));

        context = this;
        nomesImage = new ArrayList<>();

        // construir url de servicio de tabs

        subdomain =Resultados.getString(Resultados.getColumnIndex("subdomain"));
        token =Resultados.getString(Resultados.getColumnIndex("token"));
        email =Resultados.getString(Resultados.getColumnIndex("user"));

        // construir url de servicio de tabs

        url = "http://"+subdomain;
        url += ".present.com.co/api/entrada/users";
        url += "?token="+token;
        url += "&email="+ email;

        // funcion que crea los tabs
        urls = new ArrayList<>();
        //
        seteartabs(url);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.ç
        //sectionpager here

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(EntradasActivity.this, MenuActivity.class);

                startActivity(i);

            }

        });

    }

    public void seteartabs(String url){

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;

        tabLayout = (TabLayout) findViewById(R.id.tabsent);
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

                //respuesta del servicio
                try {
                    responseStr = new String(responseBody, "UTF-8");
                    // manejo del primer nivel de objetos

                    JSONArray items = new JSONArray(responseStr);

                    for(int i=0; i < items.length(); i++) {

                        String item = items.getString(i);

                        JSONObject valores = new JSONObject(item);

                        image_user = valores.getString("image");
                        entradas_url = valores.getString("entradas_url");
                        notification_count = valores.getInt("notification_count");

                        urls.add(entradas_url);

                        nomesImage.add(image_user);

                    }

                    for (int a = 0; a< nomesImage.size(); a++){
                        /////////////////////creacion de tabs dinamicamente//////////////////////
                        new DownloadImage().execute(nomesImage.get(a));
                    }

                    progressDialog[0].dismiss();

                    /////////////////////creacion de tabs dinamicamente//////////////////////

                    // tabCreate(tabLayout,"1");

                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                    // Set up the ViewPager with the sections adapter.
                    mViewPager = (ViewPager) findViewById(R.id.viewpagerent);
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
                            Toast.makeText(EntradasActivity.this, mensaje, Toast.LENGTH_LONG).show();
                        }

                        midb.logouth();
                        midb.oncreateusers();
                        Intent i = new Intent(EntradasActivity.this, InicioActivity.class);
                        startActivity(i);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // When Http response code is '500'
                else if (statusCode == 500) {

                    Toast.makeText(EntradasActivity.this, "Erros Statuscode = 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Log.i("On Failure", "NN");
                    Toast.makeText(EntradasActivity.this, "On Failure ", Toast.LENGTH_LONG).show();

                    //Institución no valida.
                }

            }

        });

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        View rootView;

        private static final int PAGE_START = 0;
        private boolean isLoading = false;
        private boolean isLastPage = false;
        private int TOTAL_PAGES = 3;
        private int currentPage = PAGE_START;
        ProgressBar progressBar;

        private static final String ARG_SECTION_NUMBER = "section_number";
        RecyclerView recyclerEntradas;
        EntradasAdapter adapter;
        public int contador;

        List<Entradas> EntradasList;

        public PlaceholderFragment() {

        }

        public static PlaceholderFragment newInstance(int sectionNumber, List<String> urls_param, String email, String token) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt("position",sectionNumber);
            args.putStringArrayList("ARG_urls", (ArrayList<String>) urls_param);
            args.putString("email", email);
            args.putString("token", token);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_entradas, container, false);
            recyclerEntradas = (RecyclerView) rootView.findViewById(R.id.recycler_entradas);

            List<String> urls_fragment = new ArrayList<>();
            //progressBar = (ProgressBar) rootView.findViewById(R.id.main_progress);

            Bundle extras = getArguments();
            final int contador = extras.getInt("position");
            urls_fragment = extras.getStringArrayList("ARG_urls");
            final String email= extras.getString("email");
            final String token= extras.getString("token");
            //Log.i("On Errno 2000 "+urls_fragment, "NN");
            // Log.e("DataDescribe", "Data: "+ urls_fragment.size());

            //   Toast.makeText(getContext(), urls_fragment.get(0), Toast.LENGTH_LONG).show();

            //scroll view

            //recyclerEntradas.setItemAnimator(new DefaultItemAnimator());

            String url = urls.get(contador-1);
            url += "?token="+token;
            url += "&email="+ email;
            // Log.e("Data: ", "Data contador " + urls.size());

            try{

                // GetEntradasData getEntradasData = new GetEntradasData(url);
                ExecuteTask executeTask = new ExecuteTask();
                executeTask.execute(url);

                JSONObject JsonDataEntrada = new JSONObject(executeTask.get());
                //Log.i("Goood", "onCreateView : "+JsonDataEntrada);
                // JSONObject dataEntradas = getEntradasData.getResponse();

                EntradasList = Contenido(contador,JsonDataEntrada);

                //Log.i("", "onCreateView: "+EntradasList);
                // Inicializar el adaptador con la fuente de datos.
                adapter = new EntradasAdapter(getContext(), EntradasList);
                recyclerEntradas.setAdapter(adapter);

            }catch (Exception e){

                Log.e("Exception", "onCreateView: "+ e);

            }
            Log.i("URL", "onCreateView: "+url);

            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            recyclerEntradas.setLayoutManager(linearLayoutManager);

            EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {

                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                    String url = urls.get(contador-1);
                    url += "&token="+token;
                    url += "&email="+ email;
                    // Log.e("Data: ", "Data contador " + urls.size());


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
                        List<Entradas> EntradasMoreList = Contenido(contador,JsonDataEntrada);
                        // adapter.notifyItemInserted(EntradasList.size() - 1);
                        EntradasList.addAll(EntradasMoreList);
                        Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                adapter.notifyItemRangeInserted(curSize,EntradasList.size() -1);
                            }
                        };

                        handler.post(r);

                    }

                }
            };

            recyclerEntradas.addOnScrollListener(scrollListener);
            // mocking network delay for API call
            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadFirstPage();
                }
            }, 1000);*/

            // fin scroll view

            return rootView;
        }

        public List<Entradas> Contenido(final int contador, JSONObject responseEntradas){

            List<Entradas> EntradasList_Contenido = new ArrayList<>();

            String id = null;
            String user_name = null;
            String curso_grupo = null;
            String created_at = null;
            String proceso_name = null;
            String detalles = null;
            String img_persona = null;
            String name = null;
            String url_entrada_detail = null;

            try {

                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");


                String urlnextpage = responseEntradas.getString("next_page_url");
                JSONArray jsonarray = new JSONArray(responseEntradas.getString("data"));
                JSONArray jsonarray_tags;

                for(int i=0; i < jsonarray.length(); i++) {

                    JSONObject jsonobject_data = jsonarray.getJSONObject(i);

                    id   = jsonobject_data.getString("id");
                    user_name   = jsonobject_data.getString("user_name");
                    curso_grupo = jsonobject_data.getString("curso_grupo");
                    created_at  = jsonobject_data.getString("created_at");
                    img_persona  = jsonobject_data.getString("user_image");
                    proceso_name = jsonobject_data.getString("proceso_name");
                    url_entrada_detail = jsonobject_data.getString("url_entrada_detail");

                    Date date = originalFormat.parse(created_at);
                    String dateref = originalFormat.format(date);

                    detalles = curso_grupo + " " + created_at;
                    jsonarray_tags = new JSONArray(jsonobject_data.getString("tags"));

                    String [] tags = new String [jsonarray_tags.length()];
                    for(int j=0; j < jsonarray_tags.length(); j++) {

                        JSONObject jsonobject_tags = jsonarray_tags.getJSONObject(j);
                        name       = jsonobject_tags.getString("name");
                        //tags.add(name);
                        tags[j] = name;
                        //Toast.makeText(VerEntradas.this, "Bien por "+name, Toast.LENGTH_LONG).show();

                    }

                    try {

                        // Log.i("Good", "onCreateView: "+ proceso_name);

                        EntradasList_Contenido.add(new Entradas(proceso_name,dateref,tags,url_entrada_detail));

                        // Toast.makeText(VerEntradas.this, "Bien por "+entrada_1.getIndice(), Toast.LENGTH_LONG).show();


                    }catch (Exception e){
                        Toast.makeText(getContext(), "Fallo por "+e, Toast.LENGTH_LONG).show();
                        Log.i("entradasError","Fallo por "+e);
                    }

                    TextView tituloentradasuser = (TextView) rootView.findViewById(R.id.tituloentradasuser);
                    TextView detalleentradasuser = (TextView) rootView.findViewById(R.id.detalleentradasuser);
                    ImageView imgPersona  = (ImageView) rootView.findViewById(R.id.img_persona);
                    tituloentradasuser.setText(user_name);
                    detalleentradasuser.setText(curso_grupo);

                    Picasso.with(getContext())
                            .load(img_persona)
                            .error(R.drawable.logo)
                            .into(imgPersona);

                }

                urls.set(contador-1, urlnextpage);

                //Log.e("Data: ", "Data Url " + urls.get(contador - 1));


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Fallo por "+e, Toast.LENGTH_LONG).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            return EntradasList_Contenido;
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position + 1, urls, email, token);


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return urls.size();
            //return 3;

        }
    }

    // funciones de descarga de imagenes
    private void setImage(Drawable drawable) {

        setBackgroundDrawable(drawable);
    }

    @Deprecated
    public void setBackgroundDrawable(Drawable drawable) {

        //logocol.setBackgroundDrawable(drawable);

        tabLayout.addTab(tabLayout.newTab().setIcon(drawable));
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

                //Drawable d = new BitmapDrawable(getResources(), bMap);


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


}
