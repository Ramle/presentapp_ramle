package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sunnysoft.presentapp.Datos.DatabaseHelper;
import sunnysoft.presentapp.Interfaz.CalendarioActivity;
import sunnysoft.presentapp.Interfaz.EntradasActivity;
import sunnysoft.presentapp.Interfaz.InicioActivity;
import sunnysoft.presentapp.Interfaz.MenuActivity;
import sunnysoft.presentapp.Interfaz.ModulosActivity;
import sunnysoft.presentapp.Interfaz.MuralesActivity;
import sunnysoft.presentapp.R;

/**
 * Created by dchizavo on 27/11/17.
 */

public class AdapterMenu  extends BaseAdapter {

    // declaracion de BD
    private DatabaseHelper midb;



    ArrayList<String> result = new ArrayList<String>();
    Context context;
    ArrayList<Integer> imageId = new ArrayList<Integer>();
    private static LayoutInflater inflater=null;
    public AdapterMenu(MenuActivity MenuActivity, ArrayList<String> prgmNameList, ArrayList<Integer> prgmImages) {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        context=MenuActivity;
        imageId = prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {

        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.grid_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.nombremenu);
        holder.img=(ImageView) rowView.findViewById(R.id.iconmenu);

        holder.tv.setText(result.get(i));
        holder.img.setImageResource(imageId.get(i));

        midb = new DatabaseHelper(context);

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Toast.makeText(context, "You Clicked "+result.get(i), Toast.LENGTH_LONG).show();
                Intent itent;

                switch (result.get(i)){



                    case "murales":
                        itent = new Intent(context, MuralesActivity.class);
                        context.startActivity(itent);
                        break;
                    case "modulos":
                        itent = new Intent(context, ModulosActivity.class);
                        context.startActivity(itent);
                        break;
                    case "entradas":
                        itent = new Intent(context, EntradasActivity.class);
                        context.startActivity(itent);
                        break;
                    case "email":
                        itent = new Intent(context, MuralesActivity.class);
                        context.startActivity(itent);
                        break;
                    case "calendario":
                        itent = new Intent(context, CalendarioActivity.class);
                        context.startActivity(itent);
                        break;

                    case "Logouth":
                        midb.logouth();
                        itent = new Intent(context, InicioActivity.class);
                        context.startActivity(itent);
                        break;
                }



            }
        });

        return rowView;


    }
}
