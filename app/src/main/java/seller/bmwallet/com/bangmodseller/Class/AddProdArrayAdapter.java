package seller.bmwallet.com.bangmodseller.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import seller.bmwallet.com.bangmodseller.R;

/**
 * Created by Thanatkorn on 9/10/2014.
 */
public class AddProdArrayAdapter extends ArrayAdapter<Product> {

    private List<Product> items;
    private int layoutResourceId;
    private Context context;
    private int layoutListId;
    private DecimalFormat format;

    public AddProdArrayAdapter(Context context, int layoutResourceId, List<Product> items, int layoutListId) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        this.layoutListId = layoutListId;
        this.format = new DecimalFormat("0.00");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(layoutListId, null);

        }

        Product p = getItem(position);


        if (p != null) {

            TextView product = (TextView) v.findViewById(R.id.prod);
            TextView price = (TextView) v.findViewById(R.id.price);


            product.setText(p.getProductName());
            price.setText(format.format(p.getPrice())+" บาท");

        }

        return v;

    }
}
