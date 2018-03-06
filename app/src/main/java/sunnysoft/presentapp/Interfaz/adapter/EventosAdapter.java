package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sunnysoft.presentapp.Interfaz.pojo.Eventos;
import sunnysoft.presentapp.Interfaz.pojo.Murales;
import sunnysoft.presentapp.R;

/**
 * Created by apple on 4/12/17.
 */

public class EventosAdapter extends ArrayAdapter<Eventos> {

    String DateRefer;

    public EventosAdapter(Context context, List<Eventos> objects, String date) {
        super(context, 0, objects);
        DateRefer = date;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;

        // ¿Ya se infló este view?
        if (null == convertView) {
            //Si no existe, entonces inflarlo con image_list_view.xml
            convertView = inflater.inflate(
                    R.layout.list_item_eventodia,
                    parent,
                    false);

            holder = new ViewHolder();
            holder.evento = (TextView) convertView.findViewById(R.id.tv_evento);
            holder.tipoevento = (TextView) convertView.findViewById(R.id.tv_tipoevento);
            holder.fechaevento = (TextView) convertView.findViewById(R.id.tv_fechaevento);
            holder.horaevento = (TextView) convertView.findViewById(R.id.tv_horaevento);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        // Actual.
        Eventos evento = getItem(position);
        // Setup.


            holder.evento.setText(evento.getEvento());

            switch (evento.getTipoevento()){

                case "entrada":
                    holder.evento.setTextColor(Color.parseColor("#DC9233"));
                    break;
                case "institucional":
                    holder.evento.setTextColor(Color.parseColor("#69B4E8"));
                    break;
            }

            holder.tipoevento.setText(evento.getTipoevento());
            holder.fechaevento.setText(evento.getFechaeventoString());
            holder.horaevento.setText(evento.getHoraevento());

        return convertView;
    }

    static class ViewHolder {
        TextView evento;
        TextView tipoevento;
        TextView fechaevento;
        TextView horaevento;
    }



}
