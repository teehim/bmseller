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
 * Created by Thanatkorn on 9/25/2014.
 */
public class AddPromoArrayAdapter extends ArrayAdapter<Promotion> {

    private List<Promotion> items;
    private int layoutResourceId;
    private Context context;
    private int layoutListId;

    public AddPromoArrayAdapter(Context context, int layoutResourceId, List<Promotion> items, int layoutListId) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        this.layoutListId = layoutListId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(layoutListId, null);

        }

        Promotion p = getItem(position);


        if (p != null) {

            TextView title = (TextView) v.findViewById(R.id.title);
            TextView point = (TextView) v.findViewById(R.id.point);


            title.setText(p.getTitle());
            point.setText(p.getUsePoint()+" point(s)");

        }

        return v;

    }
}
