package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import sunnysoft.presentapp.Interfaz.pojo.FieldsEntradas;
import sunnysoft.presentapp.R;


/**
 * Created by dchizavo on 1/02/18.
 */

public class FieldsEntradasAdapter extends ArrayAdapter<FieldsEntradas> {


    public FieldsEntradasAdapter(Context context, List<FieldsEntradas> objects) {
        super(context, 0, objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FieldsEventoAdapter.ViewHolder holder;

        // ¿Ya se infló este view?
        if (null == convertView) {
            //Si no existe, entonces inflarlo con image_list_view.xml
            convertView = inflater.inflate(
                    R.layout.list_items_verfieldsentradas,
                    parent,
                    false);

            holder = new FieldsEventoAdapter.ViewHolder();
            holder.fieldtitulo = (TextView) convertView.findViewById(R.id.campotitulo);
            holder.fielddetalle = (TextView) convertView.findViewById(R.id.campodetalle);

            convertView.setTag(holder);

        } else {

            holder = (FieldsEventoAdapter.ViewHolder) convertView.getTag();

        }

        // Actual.
        FieldsEntradas fieldsEntradas = getItem(position);
        // Setup.

        holder.fieldtitulo.setText(fieldsEntradas.getTitulo());
        holder.fielddetalle.setText(fieldsEntradas.getDetalle());


        return convertView;
    }

    static class ViewHolder {

        TextView fieldtitulo;
        TextView fielddetalle;
    }


}

