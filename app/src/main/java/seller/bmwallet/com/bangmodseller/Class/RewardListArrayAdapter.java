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
 * Created by Thanatkorn on 9/23/2014.
 */
public class RewardListArrayAdapter extends ArrayAdapter<Reward> {

    private List<Reward> items;
    private int layoutResourceId;
    private Context context;
    private int layoutListId;

    public RewardListArrayAdapter(Context context, int layoutResourceId, List<Reward> items, int layoutListId) {
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

        Reward r = getItem(position);


        if (r != null) {

            TextView name = (TextView) v.findViewById(R.id.reward_name);
            TextView price = (TextView) v.findViewById(R.id.show_price);
            TextView amount = (TextView) v.findViewById(R.id.show_amount);


            name.setText(r.getProductName());
            price.setText(r.getPrice() + " baht");
            amount.setText(r.getAmount()+ " piece(s)");

        }

        return v;

    }
}
