package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sunnysoft.presentapp.Interfaz.CreateentradaActivity;
import sunnysoft.presentapp.Interfaz.pojo.Nivel_Tres;
import sunnysoft.presentapp.R;

/**
 * Created by gustavo on 25/11/17.
 */

public class Nivel_Tres_Adapter extends RecyclerView.Adapter<Nivel_Tres_Adapter.ViewHolder>{

    Context context;
    List<Nivel_Tres>nivelTresList;

    public Nivel_Tres_Adapter(Context context, List<Nivel_Tres> nivelTresList) {
        this.context = context;
        this.nivelTresList = nivelTresList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nivel_tres, parent, false);
        final Nivel_Tres_Adapter.ViewHolder viewHolder = new Nivel_Tres_Adapter.ViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.desplegar();

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titulo.setText(nivelTresList.get(position).getNombre());
        holder.url = nivelTresList.get(position).getUrl();
        holder.nombre = nivelTresList.get(position).getNombre();

    }

    @Override
    public int getItemCount() {
        return nivelTresList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView titulo;
        private String url;
        private String nombre;


        public ViewHolder(View itemView) {
            super(itemView);

            titulo = (TextView)itemView.findViewById(R.id.txv_titulo);
        }

        public void desplegar(){

            Intent i = new Intent(context, CreateentradaActivity.class);
            i.putExtra("url", url);
            i.putExtra("nombre", nombre);
            context.startActivity(i);

        }


    }
}
