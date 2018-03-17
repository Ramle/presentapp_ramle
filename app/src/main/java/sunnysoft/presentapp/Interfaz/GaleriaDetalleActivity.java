package sunnysoft.presentapp.Interfaz;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import sunnysoft.presentapp.Interfaz.adapter.GaleriaAdapter;
import sunnysoft.presentapp.R;

public class GaleriaDetalleActivity extends AppCompatActivity {

    private GaleriaAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria_detalle);

        viewPager = (ViewPager) findViewById(R.id.pager);
        Intent i = getIntent();
        int position = i.getIntExtra("position",0);

        /////// aqui resive la lista de urls del servicio que paso por el adaptador del grid
        //// y ese se lo envia a esta actividad para pasar la lista al adaptador del pager
        ArrayList<String> url = i.getStringArrayListExtra("urls");

        adapter=new GaleriaAdapter(this,url);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }
}
