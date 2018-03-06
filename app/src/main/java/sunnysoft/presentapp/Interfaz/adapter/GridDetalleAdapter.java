package sunnysoft.presentapp.Interfaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import sunnysoft.presentapp.Interfaz.GaleriaDetalleActivity;
import sunnysoft.presentapp.R;

/**
 * Created by gustavo on 17/12/17.
 */

public class GridDetalleAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> _urls = new ArrayList<>();

    public GridDetalleAdapter(Context context, ArrayList<String> _urls) {
        this.context = context;
        this._urls = _urls;
    }

    @Override
    public int getCount() {
        return _urls.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null){
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(500, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }else{
            imageView = (ImageView) view;
        }

        Picasso.with(context).load(_urls.get(i)).error(R.drawable.logo).into(imageView);
        imageView.setOnClickListener(new OnImageClickListener(i));
        return imageView ;
    }

    class OnImageClickListener implements View.OnClickListener {

        int _position;

        public OnImageClickListener(int _position) {
            this._position = _position;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, GaleriaDetalleActivity.class);
            i.putExtra("position",_position);
            i.putStringArrayListExtra("urls",_urls);
            context.startActivity(i);

        }
    }
}
