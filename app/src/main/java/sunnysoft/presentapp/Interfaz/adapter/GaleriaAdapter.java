package sunnysoft.presentapp.Interfaz.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import sunnysoft.presentapp.R;

/**
 * Created by gustavo on 17/12/17.
 */

public class GaleriaAdapter extends PagerAdapter {

    Context context;
    Activity _activity;
    ArrayList<String> _urls;
    LayoutInflater layoutInflater;

    public GaleriaAdapter(Context context, ArrayList<String> _urls) {
        this.context = context;
        this._urls = _urls;
    }

    @Override
    public int getCount() {
        return _urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position){
        ImageView imgDisplay;
        Button close;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = layoutInflater.inflate(R.layout.item_galeria,container,false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imagen);
        close = (Button) viewLayout.findViewById(R.id.btn_cerrar);

        Picasso.with(context).load(_urls.get(position)).error(R.drawable.icono_menu).networkPolicy(NetworkPolicy.OFFLINE).into(imgDisplay);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity)(context)).finish();
            }
        });
        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ConstraintLayout) object);

    }
}

