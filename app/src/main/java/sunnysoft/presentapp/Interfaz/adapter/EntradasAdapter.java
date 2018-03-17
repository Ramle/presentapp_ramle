package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import sunnysoft.presentapp.Interfaz.CalendarioActivity;
import sunnysoft.presentapp.Interfaz.DetalleentradaActivity;
import sunnysoft.presentapp.Interfaz.DetalleentradagralActivity;
import sunnysoft.presentapp.Interfaz.VereventoActivity;
import sunnysoft.presentapp.Interfaz.pojo.Entradas;
import sunnysoft.presentapp.Interfaz.pojo.Murales;
import sunnysoft.presentapp.R;

/**
 * Created by dchizavo on 12/12/17.
 */

public class EntradasAdapter extends RecyclerView.Adapter<EntradasAdapter.ViewHolder> {

    Context context;
    List<Entradas>entradasList;
    private boolean isLoadingAdded = false;

    public EntradasAdapter(Context context, List<Entradas> entradasList) {
        this.context = context;
        this.entradasList = entradasList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entradas, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.titulo.setText(entradasList.get(position).getNombre());
        holder.detalle.setText(entradasList.get(position).getFecha());
        holder.mTagGroup.setTags(entradasList.get(position).getTags());
        Log.e("", "onBindViewHolder: "+ entradasList.get(position).getNombre());
        //Toast.makeText(context, "dato "+entradasList.get(position).getImage_persona(), Toast.LENGTH_LONG).show();


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


        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            titulo = (TextView) itemView.findViewById(R.id.tituloverentradas);
            detalle = (TextView) itemView.findViewById(R.id.detalleverentradas);
            mTagGroup = (TagGroup)itemView.findViewById(R.id.tag_group_entradas);
        }

        @Override
        public void onClick(View view) {

            Log.e("Data clicked", "onClick " +  entradasList.get(getPosition()).getUrl_entrada_detail());

            //Intent i = new Intent(context, DetalleentradaActivity.class);
            /*i.putExtra("DetailUrl",  entradasList.get(getPosition()).getUrl_entrada_detail());
            i.putExtra("tURL",entradasList.get(getPosition()).getUrl_tabs_entradas());
            context.startActivity(i);*/

            Intent i = new Intent(context, DetalleentradagralActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("tURL",entradasList.get(getPosition()).getUrl_tabs_entradas());
            i.putExtra("url",  entradasList.get(getPosition()).getUrl_entrada_detail());
            context.startActivity(i);

        }
    }


    public Entradas getItem(int position) {
        return entradasList.get(position);
    }


}
