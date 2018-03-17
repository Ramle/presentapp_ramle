package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sunnysoft.presentapp.Interfaz.pojo.Nivel_Dos;
import sunnysoft.presentapp.Interfaz.pojo.Nivel_Tres;
import sunnysoft.presentapp.Interfaz.pojo.Nivel_uno;
import sunnysoft.presentapp.R;

/**
 * Created by gustavo on 25/11/17.
 */

public class Nivel_Uno_Adapter extends RecyclerView.Adapter<Nivel_Uno_Adapter.ViewHolder> {

    private int Indicador;
    Context context;
    List<Nivel_uno>nivelUnoList;
    List<Nivel_Dos>nivelDosList;

    public Nivel_Uno_Adapter(Context context, List<Nivel_uno> nivelUnoList, List<Nivel_Dos> nivelDosList) {
        this.context = context;
        this.nivelUnoList = nivelUnoList;
        this.nivelDosList = nivelDosList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_primer_nivel, parent, false);
        final Nivel_Uno_Adapter.ViewHolder viewHolder = new Nivel_Uno_Adapter.ViewHolder(itemView);

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
        holder.titulo.setText(nivelUnoList.get(position).getNombre());
        holder.lista2.clear();
        String llave2 = nivelDosList.get(position).getNombre();
        try {
            JSONArray procesos = new JSONArray(llave2);
            for(int a=0; a < procesos.length(); a++) {

                String fila = procesos.getString(a);
                JSONObject objeto3 = new JSONObject(fila);
                String nombreproceso = objeto3.getString("nombre");
                String view_all_url = objeto3.getString("view_all_url");
                holder.lista2.add(new Nivel_Dos(nombreproceso,view_all_url));
                String cursos = objeto3.getString("items");
                holder.lista3.add(new Nivel_Tres(cursos,"null"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return nivelUnoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView titulo;
        private ConstraintLayout contenedor_nivel_dos, cont_titulo;
        private RecyclerView recycler_segundo_nivel;
        private ColorStateList oldColors;
        private ImageView img;
        public List<Nivel_Dos> lista2 = new ArrayList<>();
        public List<Nivel_Tres> lista3 = new ArrayList<>();

        public ViewHolder(View itemView) {
            super(itemView);

            titulo = (TextView)itemView.findViewById(R.id.txv_item);
            contenedor_nivel_dos = (ConstraintLayout)itemView.findViewById(R.id.container_nivel_dos);
            cont_titulo = (ConstraintLayout)itemView.findViewById(R.id.cont_titulo);
            img = (ImageView)itemView.findViewById(R.id.flecha_mostrar);
            recycler_segundo_nivel = (RecyclerView)itemView.findViewById(R.id.recycler_segundo_nivel);
            Nivel_Dos_Adapter adapter;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recycler_segundo_nivel.setLayoutManager(linearLayoutManager);



            adapter = new Nivel_Dos_Adapter(itemView.getContext(),lista2, lista3);
            recycler_segundo_nivel.setAdapter(adapter);
            oldColors = titulo.getTextColors();

        }

        public void desplegar(){
            if (contenedor_nivel_dos.getVisibility() == View.GONE){
                contenedor_nivel_dos.setVisibility(View.VISIBLE);
                cont_titulo.setBackgroundResource(R.color.color_botones_primarios);
                titulo.setTextColor(context.getResources().getColor(R.color.color_input));
                img.setImageDrawable(itemView.getResources().getDrawable(R.drawable.arrow_selected));
            }else{
                contenedor_nivel_dos.setVisibility(View.GONE);
                cont_titulo.setBackgroundResource(R.color.color_letra_in_prim);
                titulo.setTextColor(oldColors);
                img.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_action_name));
            }
        }
    }
}
