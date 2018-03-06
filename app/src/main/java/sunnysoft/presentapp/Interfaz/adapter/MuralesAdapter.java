package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sunnysoft.presentapp.Interfaz.DetalleMuralesActivity;
import sunnysoft.presentapp.Interfaz.pojo.Murales;
import sunnysoft.presentapp.R;

/**
 * Created by gustavo on 13/11/17.
 */

public class MuralesAdapter extends RecyclerView.Adapter<MuralesAdapter.ViewHolder> {

    Context context;
    List<Murales>muralesList;
    HashMap<String,String> fil = new HashMap<>();
    HashMap<String,String> pho = new HashMap<>();

    public MuralesAdapter(Context context, List<Murales> muralesList) {
        this.context = context;
        this.muralesList = muralesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_murales, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txvNombre.setText(muralesList.get(position).getNombre());
        holder.txvFecha.setText(muralesList.get(position).getFecha());
        holder.web.getSettings().setJavaScriptEnabled(true);
        holder.web.loadDataWithBaseURL(null,muralesList.get(position).getContenido(),"text/html","utf-8",null);
        fil = muralesList.get(position).getFiles();
        pho= muralesList.get(position).getPhotos();

        if (muralesList.get(position).getreadmore().equals("false")){

            holder.txtreadmore.setVisibility(View.GONE);

        }else{
            holder.txtreadmore.setVisibility(View.VISIBLE);
            holder.txtreadmore.setOnClickListener(new IntemClickListener(muralesList.get(position).getUrl_detalle()));
        }
        //Log.e("photuser", muralesList.get(position).getImagen_persona());

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(2)
                .cornerRadiusDp(30)
                .oval(false)
                .build();


        Picasso.with(context)
                .load(muralesList.get(position).getImagen_persona())
                .fit()
                .transform(transformation)
                .error(R.drawable.logo)
                .into(holder.imgPersona);



        if (muralesList.get(position).isAdjuntos()){
            holder.contAdjuntos.setVisibility(View.VISIBLE);

            if (fil.size() > 2) {
                //Log.e("tamaño", String.valueOf(fil.size()));
                ViewGroup.LayoutParams layoutParams = holder.contAdjuntos.getLayoutParams();
                layoutParams.height = 250; //this is in pixels
                holder.contAdjuntos.setLayoutParams(layoutParams);
            }else {

                ViewGroup.LayoutParams layoutParams = holder.contAdjuntos.getLayoutParams();
                layoutParams.height = 125; //this is in pixels
                holder.contAdjuntos.setLayoutParams(layoutParams);

            }

            AdjuntosAdapter adapter = new AdjuntosAdapter(context,fil);
            holder.grid.setAdapter(adapter);


        }else{
            holder.contAdjuntos.setVisibility(View.GONE);

        }

        if (muralesList.get(position).isAdjutos_imagen()){

            holder.contImgAdjuntos.setVisibility(View.VISIBLE);
            Set setphotos = pho.entrySet();
            Iterator iteratorphotos = setphotos.iterator();
            int inicio = 0;
            while (iteratorphotos.hasNext()){
                inicio = inicio +1;
                Map.Entry photo_item = (Map.Entry)iteratorphotos.next();

                if (inicio == 1){
                    Picasso.with(context).load(photo_item.getValue().toString()).fit().error(R.drawable.logo).into(holder.adjImg1);

                } else if (inicio == 2){
                    Picasso.with(context).load(photo_item.getValue().toString()).fit().error(R.drawable.logo).into(holder.adjImg2);
                    //break;

                } else if (inicio == 3){
                    //Picasso.with(context).load(R.drawable.logo).error(R.drawable.logo).into(holder.adjImg2);
                    //holder.adjImg2.setImageResource(R.drawable.logo);
                    break;
                }

                if (pho.size() == 1){
                    holder.adjImg2.setVisibility(View.GONE);
                } else if (pho.size()>2){
                    int mas = pho.size() - 2;
                    holder.cont_vermas.setVisibility(View.VISIBLE);
                    holder.num_vermas.setText(""+mas+"+");
                }else{
                    holder.cont_vermas.setVisibility(View.GONE);

                }

            }
            holder.adjImg1.setOnClickListener(new IntemClickListener(muralesList.get(position).getUrl_detalle()));
            holder.adjImg2.setOnClickListener(new IntemClickListener(muralesList.get(position).getUrl_detalle()));


        }else{
            holder.contImgAdjuntos.setVisibility(View.GONE);

        }




    }

    @Override
    public int getItemCount() {
        return muralesList.size();
    }

    class IntemClickListener implements View.OnClickListener{
        String url;

        public IntemClickListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, DetalleMuralesActivity.class);
            i.putExtra("servicio",url);
            context.startActivity(i);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout contImgAdjuntos;
        private ImageView adjImg3;
        private ImageView adjImg2;
        private ImageView adjImg1;
        private ConstraintLayout contAdjuntos;
        private WebView web;
        private TextView txvFecha;
        private TextView txvNombre;
        private ImageView imgPersona;
        private TextView txtreadmore;
        private GridView grid;
        private TextView num_vermas;
        private ConstraintLayout cont_vermas;


        public ViewHolder(View itemView) {
            super(itemView);

            web = (WebView)itemView.findViewById(R.id.web_contenido);
            txvFecha = (TextView)itemView.findViewById( R.id.txv_fecha );
            txvNombre = (TextView)itemView.findViewById( R.id.txv_nombre );
            contAdjuntos = (ConstraintLayout)itemView.findViewById( R.id.cont_adjuntos );
            contImgAdjuntos = (ConstraintLayout)itemView.findViewById( R.id.cont_img_adjuntos );
            imgPersona = (ImageView)itemView.findViewById(R.id.img_persona);
            txtreadmore = (TextView)itemView.findViewById(R.id.readmore);
            adjImg1 = (ImageView)itemView.findViewById(R.id.adj_img_1);
            adjImg2 = (ImageView)itemView.findViewById(R.id.adj_img_2);
            //adjImg3 = (ImageView)itemView.findViewById(R.id.adj_img_3);
            grid = (GridView)itemView.findViewById(R.id.grid_files_adj);
            num_vermas =(TextView)itemView.findViewById(R.id.num_vermas);
            cont_vermas = (ConstraintLayout)itemView.findViewById(R.id.cont_num_vermas);


        }

    }
}
