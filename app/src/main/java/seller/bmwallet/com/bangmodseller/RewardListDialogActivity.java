package seller.bmwallet.com.bangmodseller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import seller.bmwallet.com.bangmodseller.Class.AddProdArrayAdapter;
import seller.bmwallet.com.bangmodseller.Class.JSONParser;
import seller.bmwallet.com.bangmodseller.Class.LineItem;
import seller.bmwallet.com.bangmodseller.Class.OrderArrayAdapter;
import seller.bmwallet.com.bangmodseller.Class.Product;
import seller.bmwallet.com.bangmodseller.Class.Reward;
import seller.bmwallet.com.bangmodseller.Class.RewardListArrayAdapter;
import seller.bmwallet.com.bangmodseller.Class.SessionManager;
import seller.bmwallet.com.bangmodseller.R;

public class RewardListDialogActivity extends FragmentActivity implements NumberPickerDialogFragment.NumberPickerDialogHandler {

    RewardListArrayAdapter adapter;
    ArrayList<Reward> rewardList;
    Reward reward = new Reward();
    ListView rewardListView;
    TextView rewardName, rewardAmount, rewardPrice;
    LinearLayout addReward;
    List<Product> rewardlistItems = null;
    List<String> listItems;
    JSONObject jObj;
    SessionManager ss;
    int indi = 0;
    Button okBtn,ccBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_list_dialog);

        ss = new SessionManager(this);
        Bundle extras = getIntent().getExtras();
        rewardList = (ArrayList<Reward>)extras.getSerializable("rewardList");
        if(rewardList.size()>0){
            setupListViewAdapter();
        }
        rewardName = (TextView)findViewById(R.id.reward_name);
        rewardName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardNameList();
            }
        });

        rewardAmount = (TextView) findViewById(R.id.reward_amount);
        rewardAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indi = 0;
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                npb.show();
            }
        });

        rewardPrice = (TextView) findViewById(R.id.reward_price);
        rewardPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indi = 1;
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                npb.show();
            }
        });

        addReward = (LinearLayout) findViewById(R.id.add_reward);
        addReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReward();
            }
        });

        okBtn = (Button) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("rewardList", rewardList);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        ccBtn = (Button)  findViewById(R.id.ccBtn);
        ccBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(RewardListDialogActivity.this,ManagePromotionActivity.class);
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

    }
    private void setupListViewAdapter() {
        adapter = new RewardListArrayAdapter(this,R.layout.reward_list_dialog,rewardList,R.layout.reward_list);
        rewardListView = (ListView) findViewById(R.id.reward_list);
        rewardListView.setAdapter(adapter);
    }

    private void addReward(){
        if(!TextUtils.isEmpty(rewardName.getText())&&!TextUtils.isEmpty(rewardPrice.getText())&&!TextUtils.isEmpty(rewardAmount.getText())) {
            rewardList.add(reward);
            setupListViewAdapter();
            reward = new Reward();
            rewardName.setText("");
            rewardPrice.setText("");
            rewardAmount.setText("  ");
        }else{

        }
    }

    public void removeReward(View v) {

        final int position = rewardListView.getPositionForView((LinearLayout) v
                .getParent());
        rewardList.remove(position);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reward_list_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showRewardNameList(){
        if(listItems!=null){
            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(RewardListDialogActivity.this);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    rewardName.setText(items[item]);
                    reward.setProductName(rewardlistItems.get(item).getProductName());
                    reward.setProductId(rewardlistItems.get(item).getId());
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            String url[] = new String[1];
            url[0] = "https://apps.bm-wallet.com/store/get_product_by_id.php";
            new GetProduct().execute(url);
        }
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
        if(isNegative==false) {
            if (indi == 0) {
                String amount = number + "";
                reward.setAmount(amount);
                rewardAmount.setText(amount);
            } else {
                DecimalFormat format = new DecimalFormat("0.00");
                String price = format.format(fullNumber);
                reward.setPrice(price);
                rewardPrice.setText(price);
            }
        }else{
            /*
            A
            L
            E
            R
            T
             */
        }
    }

    private class GetProduct extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... urls) {
            String url = urls[0];
            jObj = new JSONObject();
            try {
                jObj.put("store_id", ss.getUserDetails().getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray json = JSONParser.postToUrlArr(url, jObj, RewardListDialogActivity.this);

            return json;
        }

        @Override
        protected void onPostExecute(JSONArray json) {
            rewardlistItems = new ArrayList<Product>();
            listItems = new ArrayList<String>();
            for (int i = json.length() - 1; i >= 0; i--) {
                Product p = new Product();
                try {
                    listItems.add(json.getJSONObject(i).getString("NAME"));
                    p.setProductName(json.getJSONObject(i).getString("NAME"));
                    p.setId(json.getJSONObject(i).getString("PRODUCTID"));
                    rewardlistItems.add(p);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            showRewardNameList();

        }
    }
}
