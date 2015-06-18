package spicesoft.appstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import spicesoft.appstore.Model.App;
import spicesoft.appstore.R;

/**
 * Created by Vincent on 08/06/15.
 */
public class CustomGridAdapter extends BaseAdapter {

    private Context context;
    private final List<App> appList;

    //Constructor to initialize values
    public CustomGridAdapter(Context context, List<App> appList) {

        this.context        = context;
        this.appList     = appList;
    }

    @Override
    public int getCount() {

        // Number of times getView method call depends upon gridValues.length
        return appList.size();
    }

    @Override
    public Object getItem(int position) {

        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }


    // Number of times getView method call depends upon gridValues.length

    public View getView(int position, View convertView, ViewGroup parent) {

        // LayoutInflator to call external grid_item.xml file

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from grid_item.xml ( Defined Below )

            gridView = inflater.inflate( R.layout.grid_item , null);

            // set value into textview

            TextView textView = (TextView) gridView
                    .findViewById(R.id.grid_item_label);

            textView.setText(appList.get(position).name);

            TextView textView1 = (TextView) gridView.findViewById(R.id.grid_item_label_static);
            textView1.setText(appList.get(position).description);

            // set image based on selected text

            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.grid_item_image);
            imageView.setImageBitmap(appList.get(position).logo);


        } else {

            gridView = (View) convertView;
        }

        return gridView;
    }
}