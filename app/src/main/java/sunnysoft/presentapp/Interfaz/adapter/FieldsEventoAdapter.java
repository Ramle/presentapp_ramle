package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;


import java.util.List;

import sunnysoft.presentapp.Interfaz.pojo.FieldsEvento;
import sunnysoft.presentapp.R;

/**
 * Created by esantopc on 20/12/17.
 */

public class FieldsEventoAdapter extends ArrayAdapter<FieldsEvento> {

    public FieldsEventoAdapter(Context context, List<FieldsEvento> objects) {
        super(context, 0, objects);

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
                    R.layout.list_items_verfieldsevento,
                    parent,
                    false);

            holder = new ViewHolder();
            holder.fieldtitulo = (TextView) convertView.findViewById(R.id.campotitulo);
            holder.fielddetalle = (TextView) convertView.findViewById(R.id.campodetalle);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        // Actual.
        FieldsEvento fieldsEvento = getItem(position);
        // Setup.

        holder.fieldtitulo.setText(fieldsEvento.getTitulo());
        holder.fielddetalle.setText(fieldsEvento.getDetalle());


        return convertView;
    }

    static class ViewHolder {

        TextView fieldtitulo;
        TextView fielddetalle;
    }

}
