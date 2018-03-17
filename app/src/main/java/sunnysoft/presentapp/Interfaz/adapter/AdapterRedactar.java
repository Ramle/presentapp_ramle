package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import sunnysoft.presentapp.Interfaz.MenuActivity;

/**
 * Created by dchizavo on 4/03/18.
 */

public class AdapterRedactar extends BaseAdapter {

    Context context;


    private static LayoutInflater inflater=null;
    public AdapterRedactar(Context context) {
        context = context;


        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount( ) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
