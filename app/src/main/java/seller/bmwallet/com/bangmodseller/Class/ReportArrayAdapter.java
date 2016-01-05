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
 * Created by Thanatkorn on 10/21/2014.
 */
public class ReportArrayAdapter extends ArrayAdapter<Report> {
    private List<Report> items;
    private int layoutResourceId;
    private Context context;
    private int layoutListId;

    public ReportArrayAdapter(Context context, int layoutResourceId, List<Report> items, int layoutListId) {
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

        Report r = getItem(position);


        if (r != null) {

            TextView name = (TextView) v.findViewById(R.id.product_name);
            TextView total = (TextView) v.findViewById(R.id.total);


            name.setText(r.getName());
            total.setText(r.getTotal() + " baht");

        }

        return v;

    }
}
