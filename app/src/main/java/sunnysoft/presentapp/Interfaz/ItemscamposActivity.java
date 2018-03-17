package sunnysoft.presentapp.Interfaz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sunnysoft.presentapp.R;


public class ItemscamposActivity extends AppCompatActivity {

    ArrayList<String> nombrecampos = new ArrayList<>();
    ArrayList<Integer> idcampos = new ArrayList<>();

    String url;



    public class Item {
        boolean checked;
        int idcampos;
        String ItemString;
        Item(int i, String t, boolean b){
          //  ItemDrawable = drawable;
            ItemString = t;
            checked = b;
            idcampos = i;
        }

        public boolean isChecked(){
            return checked;
        }

        public int idcampos() { return idcampos;
        }
        public String ItemString() { return ItemString;
        }
    }

    static class ViewHolder {
        CheckBox checkBox;
//        ImageView icon;
        TextView text;
    }

    public class ItemsListAdapter extends BaseAdapter {

        private Context context;
        private List<Item> list;

        ItemsListAdapter(Context c, List<Item> l) {
            context = c;
            list = l;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean isChecked(int position) {
            return list.get(position).checked;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            // reuse views
            ViewHolder viewHolder = new ViewHolder();
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.row, null);

                viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.rowCheckBox);
                //viewHolder.icon = (ImageView) rowView.findViewById(R.id.rowImageView);
                viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            //viewHolder.icon.setImageDrawable(list.get(position).ItemDrawable);
            viewHolder.checkBox.setChecked(list.get(position).checked);

            final String itemStr = list.get(position).ItemString;
            viewHolder.text.setText(itemStr);

            viewHolder.checkBox.setTag(position);

           viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean newState = !list.get(position).isChecked();
                    list.get(position).checked = newState;



                    /*Toast.makeText(getApplicationContext(),
                            itemStr + "setOnClickListener\nchecked: " + newState,
                            Toast.LENGTH_LONG).show();*/
                }
            });

            viewHolder.checkBox.setChecked(isChecked(position));

            return rowView;
        }
    }

    Button btnLookup;
    List<Item> items;
    ListView listView;
    ItemsListAdapter myItemsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemscampos);
        listView = (ListView)findViewById(R.id.listview);
        btnLookup = (Button)findViewById(R.id.lookup);

        final ArrayList<String> Selecnom = new ArrayList<>();
        final ArrayList<Integer> Selecid = new ArrayList<>();

        Bundle datos = getIntent().getExtras();

        nombrecampos = datos.getStringArrayList("campos");
        idcampos = datos.getIntegerArrayList("idcampos");
        url = datos.getString("url");



        initItems();
        myItemsListAdapter = new ItemsListAdapter(this, items);
        listView.setAdapter(myItemsListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /*Toast.makeText(ItemscamposActivity.this,
                        ((Item)(parent.getItemAtPosition(position))).ItemString,
                        Toast.LENGTH_LONG).show();*/
            }});

        btnLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "Check items:\n";

                for (int i=0; i<items.size(); i++){
                    if (items.get(i).isChecked()){
                        Selecid.add(items.get(i).idcampos());
                        Selecnom.add(items.get(i).ItemString());
                    }
                }
                Intent i = new Intent(ItemscamposActivity.this, CreateentradaActivity.class);
                i.putStringArrayListExtra("campos", (ArrayList<String>) Selecnom);
                i.putIntegerArrayListExtra("idcampos", (ArrayList<Integer>) Selecid);
                i.putExtra("url", url);
                startActivity(i);

            }
        });
    }

    private void initItems(){
        items = new ArrayList<Item>();
        items.clear();

        for(int i=0; i<nombrecampos.size(); i++){


            String s = nombrecampos.get(i);
            Integer d = idcampos.get(i);

            boolean b = false;
            Item item = new Item(d, s, b);
            items.add(item);
        }

        nombrecampos.clear();
        idcampos.clear();

    }

}