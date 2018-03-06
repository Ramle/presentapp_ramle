package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sunnysoft.presentapp.Interfaz.DetalleMuralesActivity;
import sunnysoft.presentapp.R;

/**
 * Created by gustavo on 23/12/17.
 */

public class AdjuntosAdapter extends BaseAdapter{


    private Context context;
    private HashMap<String,String> files = new HashMap<>();
    private static LayoutInflater inflater=null;
    private ArrayList<String> nombres = new ArrayList<>();
    private ArrayList<String> url = new ArrayList<>();

    public AdjuntosAdapter(Context context, HashMap<String, String> files) {
        this.context = context;
        this.files = files;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        Set setfiles = files.entrySet();
        Iterator iterator = setfiles.iterator();
        while(iterator.hasNext()){
            Map.Entry file_item = (Map.Entry)iterator.next();
            Log.i("Nombres",file_item.getKey().toString());
            nombres.add(file_item.getKey().toString());
            url.add(file_item.getValue().toString());
        }
        Log.i("Numero",""+files.size());
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder{
        Button btn_adj;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = new Holder();
        View itemView;

        itemView = inflater.inflate(R.layout.item_grid_files_adj,null);

        holder.btn_adj = (Button)itemView.findViewById(R.id.btn_adj);
        holder.btn_adj.setText(nombres.get(i));
        //Log.i("LINKS",url.get(i));
        holder.btn_adj.setOnClickListener(new BotonClicListener(i));

        return itemView;
    }

    class BotonClicListener implements View.OnClickListener{
        int pos;

        public BotonClicListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url.get(pos)));
            context.startActivity(i);
        }
    }

}
