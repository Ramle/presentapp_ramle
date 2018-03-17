package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import sunnysoft.presentapp.Interfaz.DetalleentradaActivity;
import sunnysoft.presentapp.Interfaz.DetalleentradagralActivity;
import sunnysoft.presentapp.Interfaz.pojo.Entradas;
import sunnysoft.presentapp.R;

/**
 * Created by esantopc on 19/12/17.
 */

public class ProcesoEntradasAdapter extends RecyclerView.Adapter<ProcesoEntradasAdapter.ViewHolder> {

    Context context;
    List<Entradas>entradasList;
    private boolean isLoadingAdded = false;

    public ProcesoEntradasAdapter(Context context, List<Entradas> entradasList) {

        this.context = context;
        this.entradasList = entradasList;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_verentradas, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.titulo.setText(entradasList.get(position).getNombre());
        holder.detalle.setText(entradasList.get(position).getDetalle());

        Picasso.with(context)
                .load(entradasList.get(position).getImage_persona())
                .error(R.drawable.logo)
                .into(holder.imgPersona);

        holder.url = entradasList.get(position).getUrl_entrada_detail();
        holder.mTagGroup.setTags(entradasList.get(position).getNomtags());

    }

    @Override
    public int getItemCount() {
        return entradasList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        TagGroup mTagGroup;
        TextView titulo;
        TextView detalle;
        ImageView imgPersona;
        String url;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            titulo = (TextView) itemView.findViewById(R.id.tituloverentradas);
            detalle = (TextView) itemView.findViewById(R.id.detalleverentradas);
            imgPersona = (ImageView)itemView.findViewById(R.id.img_persona);
            mTagGroup = (TagGroup)itemView.findViewById(R.id.tag_group_entradas);

        }

        @Override
        public void onClick(View view) {

            try{

                Intent i = new Intent(context, DetalleentradaActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("DetailUrl", url);
                context.startActivity(i);


            }catch (Exception e){
                Log.e("", "onClick: "+e);
                Toast.makeText(context, "Fallo por NN "+e, Toast.LENGTH_LONG).show();

            }

        }

        public Entradas getItem(int position) {
            return entradasList.get(position);
        }

       /* @Override
        public void onClick(View view) {

            Log.e("Data clicked", "onClick " +  entradasList.get(getPosition()).getUrl_entrada_detail());

            Intent i = new Intent(context, VereventoActivity.class);
            i.putExtra("DetailUrl",  entradasList.get(getPosition()).getUrl_entrada_detail());
            context.startActivity(i);

        }*/
    }

}
