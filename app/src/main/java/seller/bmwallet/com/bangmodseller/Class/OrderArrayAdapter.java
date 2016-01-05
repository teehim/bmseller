package seller.bmwallet.com.bangmodseller.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import seller.bmwallet.com.bangmodseller.R;

/**
 * Created by Thanatkorn on 9/10/2014.
 */
public class OrderArrayAdapter extends ArrayAdapter<LineItem> {

    private List<LineItem> items;
    private int layoutResourceId;
    private Context context;

    public OrderArrayAdapter(Context context, int layoutResourceId, List<LineItem> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.order_list, null);

        }

        LineItem p = getItem(position);

        if (p != null) {

            TextView tt = (TextView) v.findViewById(R.id.prod);
            TextView tt1 = (TextView) v.findViewById(R.id.price);

            if (tt != null) {
                tt.setText(p.product.getProductName());
            }
            if (tt1 != null) {

                tt1.setText(p.amount+"");
            }
        }

        return v;

    }

}