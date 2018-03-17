package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sunnysoft.presentapp.Interfaz.VerEntradas;
import sunnysoft.presentapp.Interfaz.entagendadiariaActivity;
import sunnysoft.presentapp.Interfaz.pojo.Nivel_Dos;
import sunnysoft.presentapp.Interfaz.pojo.Nivel_Tres;
import sunnysoft.presentapp.R;

/**
 * Created by gustavo on 25/11/17.
 */

public class Nivel_Dos_Adapter extends RecyclerView.Adapter<Nivel_Dos_Adapter.ViewHolder>{

    Context context;
    List<Nivel_Dos>nivelDosList;
    List<Nivel_Tres>nivel_tresList;
    String View_all_url;

    public Nivel_Dos_Adapter(Context context, List<Nivel_Dos> nivelDosList, List<Nivel_Tres> nivel_tresList) {
        this.context = context;
        this.nivelDosList = nivelDosList;
        this.nivel_tresList = nivel_tresList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_segundo_nivel, parent, false);

        final Nivel_Dos_Adapter.ViewHolder viewHolder = new Nivel_Dos_Adapter.ViewHolder(itemView);


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
        holder.titulo.setText(nivelDosList.get(position).getNombre());
        View_all_url = nivelDosList.get(position).getView_all_url();

        holder.lista3.clear();
        String llave2 = nivel_tresList.get(position).getNombre();

        //holder.lista3.add(new Nivel_Tres(llave2));

        try {
            JSONArray procesos = new JSONArray(llave2);
            for(int a=0; a < procesos.length(); a++) {

                String fila = procesos.getString(a);
                JSONObject objeto3 = new JSONObject(fila);
                String nombreproceso = objeto3.getString("nombre");
                String url = objeto3.getString("url");
                holder.lista3.add(new Nivel_Tres(nombreproceso, url));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return nivelDosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView titulo;
        private ConstraintLayout contenerdor_nivel_tres, cont_titulo;
        private RecyclerView recycler_tercer_nivel;
        private List<Nivel_Tres> lista3 = new ArrayList<>();
        private ColorStateList oldColors;
        private ImageView img;
        private Button btn_entradas;


        public ViewHolder(View itemView) {
            super(itemView);
            titulo = (TextView)itemView.findViewById(R.id.txv_titulo);
            btn_entradas = (Button)itemView.findViewById(R.id.btn_entradas);
            contenerdor_nivel_tres = (ConstraintLayout)itemView.findViewById(R.id.container_nivel_tres);
            cont_titulo = (ConstraintLayout)itemView.findViewById(R.id.cont_titulo);
            img = (ImageView)itemView.findViewById(R.id.flecha_mostrar);
            recycler_tercer_nivel = (RecyclerView)itemView.findViewById(R.id.recycler_tercer_nivel);
            Nivel_Tres_Adapter adapter;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recycler_tercer_nivel.setLayoutManager(linearLayoutManager);
            Entradas();

            adapter = new Nivel_Tres_Adapter(itemView.getContext(),lista3);
            recycler_tercer_nivel.setAdapter(adapter);

            oldColors = titulo.getTextColors();
        }

        private void Entradas(){
            btn_entradas.setText("Ver entradas");
            btn_entradas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, VerEntradas.class);
                    i.putExtra("View_all_url", View_all_url);
                    context.startActivity(i);

                }
            });

        }


        public void desplegar(){
            if (contenerdor_nivel_tres.getVisibility() == View.GONE){
                contenerdor_nivel_tres.setVisibility(View.VISIBLE);
                cont_titulo.setBackgroundResource(R.color.color_botones_primarios);
                titulo.setTextColor(context.getResources().getColor(R.color.color_input));
                img.setImageDrawable(itemView.getResources().getDrawable(R.drawable.arrow_selected));
            }else{
                contenerdor_nivel_tres.setVisibility(View.GONE);
                cont_titulo.setBackgroundResource(R.color.color_letra_in_prim);
                titulo.setTextColor(oldColors);
                img.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_action_name));
            }
        }
    }
}
