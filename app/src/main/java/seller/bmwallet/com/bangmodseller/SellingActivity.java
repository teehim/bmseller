package seller.bmwallet.com.bangmodseller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import seller.bmwallet.com.bangmodseller.Class.AddProdArrayAdapter;
import seller.bmwallet.com.bangmodseller.Class.JSONParser;
import seller.bmwallet.com.bangmodseller.Class.LineItem;
import seller.bmwallet.com.bangmodseller.Class.OrderArrayAdapter;
import seller.bmwallet.com.bangmodseller.Class.Product;
import seller.bmwallet.com.bangmodseller.Class.SessionManager;
import seller.bmwallet.com.bangmodseller.Class.Transaction;


public class SellingActivity extends Activity implements CardReader.AccountCallback {

    public static final String TAG = "SellingActivity";

    private OrderArrayAdapter adapter;
    private ArrayList<LineItem> arline = new ArrayList<LineItem>();
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    public CardReader mCardReader;
    ArrayList<Product> list = new ArrayList<Product>();
    JSONObject jObj;
    ListView lv1;
    ListView lv2;
    TextView total;
    SessionManager ss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling);

        ss = new SessionManager(this);
        lv1 = (ListView) findViewById(R.id.months_list);
        lv2 = (ListView) findViewById(R.id.show_list);

        String url[] = new String[1];
        url[0] = "https://apps.bm-wallet.com/store/get_product_by_id.php";
        new GetProduct().execute(url);


        mCardReader = new CardReader(this);

        // Disable Android Beam and register our card reader callback
        enableReaderMode();
    }

    public void addProd(View v) {
        final int position = lv1.getPositionForView((LinearLayout) v
                .getParent());

        Product item = list.get(position);
		/*
		 * TextView t1 = (TextView)findViewById(R.id.prod1); TextView t2 =
		 * (TextView)findViewById(R.id.prod2);
		 *
		 * t1.setText(item.getTransProd()); t2.setText(item.getTransPrice());
		 */
        arline = LineItem.addLine(arline, item);
        total = (TextView) findViewById(R.id.total);
        total.setText("฿ " + LineItem.calTotal(arline));
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.selling, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */

    private void setupListViewAdapter() {
        adapter = new OrderArrayAdapter(SellingActivity.this, R.layout.activity_selling,
                arline);
        ListView atomPaysListView = (ListView) findViewById(R.id.show_list);
        atomPaysListView.setAdapter(adapter);
    }

    public void removeProd(View v) {


        final int position = lv2.getPositionForView((LinearLayout) v
                .getParent());
        LineItem.removeLine(arline, position);
        TextView total = (TextView) findViewById(R.id.total);
        total.setText("฿ " + LineItem.calTotal(arline));
        adapter.notifyDataSetChanged();
    }

    private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        Activity activity = SellingActivity.this;
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.enableReaderMode(activity, mCardReader, READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        Activity activity = SellingActivity.this;
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.disableReaderMode(activity);
        }
    }

    @Override
    public void onAccountReceived(String account) {

        String customerId = null,balance = null,promoId=null,storeId=null,point = null;
        char type = account.charAt(0);
        account = account.substring(1);
        String[] out = account.split(",");
        System.out.println(out[0]);
        String url[] = new String[1];
        customerId = out[0];

        if(type == '1'){

            storeId = out[2];
            promoId = out[1];
            point = out[3];

            jObj = new JSONObject();
            try {
                jObj.put("store_id", ss.getUserDetails().getUserId());
                jObj.put("customer_id", customerId);
                jObj.put("promo_id", promoId);
                jObj.put("point", point);

                url[0] = "https://apps.bm-wallet.com/store/use_promotion.php";

                new SendPointTrans().execute(url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(type == '0') {
            balance = out[1];

            JSONArray lineJson = new JSONArray();
            JSONObject item = null;
            if (arline.size() != 0) {
                for (int i = 0; i < arline.size(); i++) {
                    item = new JSONObject();
                    Product p = arline.get(i).getProduct();
                    try {
                        item.put("PRODUCT_ID", p.getId());
                        item.put("PRICE", p.getPrice() + "");
                        item.put("POINT", p.getPoint());
                        item.put("AMOUNT", arline.get(i).getAmount());
                        lineJson.put(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                jObj = new JSONObject();
                try {
                    jObj.put("store_id", ss.getUserDetails().getUserId());
                    jObj.put("customer_id", customerId);
                    jObj.put("lineitem", lineJson);
                    jObj.put("total", LineItem.calTotal(arline));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                url[0] = "https://apps.bm-wallet.com/store/update_transaction.php";

                new SendOrder().execute(url);
            }
        }

    }

    private class GetProduct extends AsyncTask<String, Void, JSONArray> {

        ProgressDialog progress = new ProgressDialog(SellingActivity.this);

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            progress.setMessage("Loading...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            String url= urls[0];
            jObj = new JSONObject();
            try {
                jObj.put("store_id",ss.getUserDetails().getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.show();
            JSONArray json = JSONParser.postToUrlArr(url, jObj, SellingActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            for(int i=json.length()-1;i>=0;i--){
                Product p = new Product();
                try {
                    p.setProductName(json.getJSONObject(i).getString("NAME"));
                    p.setId(json.getJSONObject(i).getString("PRODUCTID"));
                    p.setProductDetail(json.getJSONObject(i).getString("DETAIL"));
                    p.setPrice(Double.parseDouble(json.getJSONObject(i).getString("PRICE")));
                    p.setPoint(json.getJSONObject(i).getString("MEMBERPOINT"));
                    p.setProductImage(json.getJSONObject(i).getString("IMAGEPATH"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(p);
            }
            progress.dismiss();
            lv1.setAdapter(new AddProdArrayAdapter(SellingActivity.this, R.layout.fragment_add_product, list,R.layout.add_list));
            setupListViewAdapter();
        }

    }

    private class SendOrder extends AsyncTask<String, Void, JSONObject> {



        @Override
        protected JSONObject doInBackground(String... urls) {
            String url= urls[0];

            JSONObject json = JSONParser.postToUrlObj(url, jObj, SellingActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            String status = "";
            try {
                status = json.getString("Result");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(status.equals("1")){
                Toast.makeText(SellingActivity.this,"Payment Successful",Toast.LENGTH_LONG).show();
            }else if(status.equals("0")){
                Toast.makeText(SellingActivity.this,"Not Enough Money",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(SellingActivity.this,"Action Fail",Toast.LENGTH_LONG).show();
            }
            arline.clear();
            total.setText("฿ 0.00");
            adapter.notifyDataSetChanged();
        }

    }

    private class SendPointTrans extends AsyncTask<String, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... urls) {
            String url= urls[0];

            JSONObject json = JSONParser.postToUrlObj(url, jObj, SellingActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json){
            String status = "";
            try {
                status = json.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(status.equals("1")){
                Toast.makeText(SellingActivity.this,"Payment Successful",Toast.LENGTH_LONG).show();
            }else if(status.equals("0")){
                Toast.makeText(SellingActivity.this,"Not Enough Point or Money",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(SellingActivity.this,"Action Fail",Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        disableReaderMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableReaderMode();
    }
}
