package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import sunnysoft.presentapp.Interfaz.responder_emailActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sunnysoft.presentapp.Interfaz.DetalleMuralesActivity;
import sunnysoft.presentapp.Interfaz.VerEntradas;
import sunnysoft.presentapp.Interfaz.pojo.CorreoDetalle;
import sunnysoft.presentapp.Interfaz.responder_emailActivity;
import sunnysoft.presentapp.R;

/**
 * Created by gustavo on 1/02/18.
 */

public class CorreosDetalleAdapter extends RecyclerView.Adapter<CorreosDetalleAdapter.ViewHolder> {

    Context context;

    List<CorreoDetalle> correoDetalles;
    private List<String> name_files = new ArrayList<>();
    private List<String> url_files = new ArrayList<>();
    private List<String> urls_images = new ArrayList<>();
    String View_all_url;

    public CorreosDetalleAdapter(Context context, List<CorreoDetalle> correoDetalles) {
        this.context = context;
        this.correoDetalles = correoDetalles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detalle_correo, parent, false);
        final CorreosDetalleAdapter.ViewHolder viewHolder = new CorreosDetalleAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.nombre.setText(correoDetalles.get(position).getNombre());
        holder.fecha.setText(correoDetalles.get(position).getFecha());
        holder.hora.setText(correoDetalles.get(position).getHora());
        Picasso.with(context).load(correoDetalles.get(position).getImagen_usuario()).error(R.drawable.logo).into(holder.img_usuario);
        holder.web_contenido.getSettings().setJavaScriptEnabled(true);
        holder.web_contenido.loadDataWithBaseURL(null,correoDetalles.get(position).getContenido(),"text/html","utf-8",null);
        name_files = correoDetalles.get(position).getName_files();
        url_files = correoDetalles.get(position).getUrl_files();
        urls_images = correoDetalles.get(position).getUrls_images();
        LayoutInflater inflater = LayoutInflater.from(context);
        View_all_url = correoDetalles.get(position).getReply();
        //if (!name_files.isEmpty() && !url_files.isEmpty()){
            for (int i=0;i<name_files.size();i++){
                View v = inflater.inflate(R.layout.boton_adjuntos,holder.contenedor_adjuntos,true);
                Button b = (Button)v.findViewById(R.id.boton_adjunto);
                b.setId(i);
                b.setText(name_files.get(i));
                b.setOnClickListener(new IntemClickListener(url_files.get(i)));
            }
        //}

        //if (!urls_images.isEmpty()){
            for (int i=0;i<urls_images.size();i++){
                Log.i("ADJUNTOS","CRea"+urls_images.size());
                //LayoutInflater inflater = LayoutInflater.from(context);
                View v = inflater.inflate(R.layout.imagen_correos,holder.contenedor_imagenes,true);
                ImageView img = (ImageView)v.findViewById(R.id.imagen_correos);
                img.setId(i);
                Picasso.with(context).load(urls_images.get(i)).error(R.drawable.logo).into(img);
                img.setOnClickListener(new IntemClickListener(urls_images.get(i)));
            }
        //}

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.desplegable.getVisibility() == View.GONE){
                    holder.desplegable.setVisibility(View.VISIBLE);
                }else{
                    holder.desplegable.setVisibility(View.GONE);
                }
            }
        });

        Log.i("DESPLEGABLE","POSISIONN: "+position+" TAMAÑO:"+correoDetalles.size());
        if (position == correoDetalles.size() - 1){
            holder.desplegable.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return correoDetalles.size();
    }

    class IntemClickListener implements View.OnClickListener{
        String url;

        public IntemClickListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
            context.startActivity(i);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nombre,fecha,hora;
        private ImageView img_usuario;
        private Button btn_responder,btn_responder_todos,btn_reenviar;
        private WebView web_contenido;
        private LinearLayout contenedor_adjuntos,contenedor_imagenes;
        private ConstraintLayout desplegable;
        private CardView card;

           public ViewHolder(View itemView) {
            super(itemView);

            nombre = (TextView)itemView.findViewById(R.id.txv_nombre);
            fecha = (TextView)itemView.findViewById(R.id.txv_fecha);
            hora = (TextView)itemView.findViewById(R.id.txv_hora);
            img_usuario = (ImageView)itemView.findViewById(R.id.img_persona);
            btn_responder = (Button)itemView.findViewById(R.id.btn_responder);
            web_contenido = (WebView)itemView.findViewById(R.id.webview_contenido);
            contenedor_adjuntos = (LinearLayout)itemView.findViewById(R.id.contenedor_adjuntos);
            contenedor_imagenes = (LinearLayout)itemView.findViewById(R.id.contenedor_imagenes);
            desplegable = (ConstraintLayout)itemView.findViewById(R.id.desplegable);
            card = (CardView)itemView.findViewById(R.id.card_correos);
            Correosdetalle();
           }

        private void Correosdetalle(){
               btn_responder.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       Intent i = new Intent(context, responder_emailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       i.putExtra("View_all_url", View_all_url);
                       context.startActivity(i);
                   }
               });



        }



    }


}
