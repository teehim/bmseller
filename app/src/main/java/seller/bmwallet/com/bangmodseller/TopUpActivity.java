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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import seller.bmwallet.com.bangmodseller.Class.JSONParser;
import seller.bmwallet.com.bangmodseller.Class.SessionManager;


public class TopUpActivity extends Activity implements CardReader.AccountCallback  {

    TextView topUpText;
    String topUpAmount = "0";
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    public CardReader mCardReader;
    JSONObject jObj;
    SessionManager ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        ss = new SessionManager(this);
        topUpText = (TextView) findViewById(R.id.top_up_amount);
        mCardReader = new CardReader(this);

        enableReaderMode();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_up, menu);
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

    private void enableReaderMode() {
        Activity activity = TopUpActivity.this;
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.enableReaderMode(activity, mCardReader, READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        Activity activity = TopUpActivity.this;
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.disableReaderMode(activity);
        }
    }

    public void topUpOnClick(View v){
        if(topUpAmount.length()<4) {
            if(topUpAmount.equals("0")){
                topUpAmount = "";
            }
            switch (v.getId()) {
                case R.id.o:
                    topUpAmount += "0";
                    break;
                case R.id.double_o:
                    if(topUpAmount.equals("") || topUpAmount.length()==4){
                        topUpAmount += "0";
                    }else {
                        topUpAmount += "00";
                    }
                    break;
                case R.id.one:
                    topUpAmount += "1";
                    break;
                case R.id.two:
                    topUpAmount += "2";
                    break;
                case R.id.three:
                    topUpAmount += "3";
                    break;
                case R.id.four:
                    topUpAmount += "4";
                    break;
                case R.id.five:
                    topUpAmount += "5";
                    break;
                case R.id.six:
                    topUpAmount += "6";
                    break;
                case R.id.seven:
                    topUpAmount += "7";
                    break;
                case R.id.eight:
                    topUpAmount += "8";
                    break;
                case R.id.nine:
                    topUpAmount += "9";
                    break;
                case R.id.backspace:
                    if(!topUpAmount.equals("0")) {
                        if(topUpAmount.length()>1){
                            topUpAmount = topUpAmount.substring(0, topUpAmount.length() - 1);
                        }else{
                            topUpAmount = "0";
                        }
                    }
                    break;
                case R.id.clear:
                    topUpAmount = "0";
                    break;

            }
            topUpText.setText(topUpAmount);
        }else{
            switch (v.getId()){
                case R.id.backspace:
                    if(!topUpAmount.equals("0")) {
                        topUpAmount = topUpAmount.substring(0, topUpAmount.length() - 1);
                        if(topUpAmount.length()==1){
                            topUpAmount = "0";
                        }
                    }
                    break;
                case R.id.clear:
                    topUpAmount = "0";
                    break;

            }
            topUpText.setText(topUpAmount);
        }
    }

    @Override
    public void onAccountReceived(String account) {
        account = account.substring(1);
        String[] out = account.split(",");
        String url[] = new String[1];
        url[0] = "https://secure.bm-wallet.com/mobile_connect/inter_topup.php";
        String customerId = out[0];
        jObj = new JSONObject();
        try {
            jObj.put("cust_id",customerId);
            jObj.put("inter_id", ss.getUserDetails().getUserId());
            jObj.put("total",topUpAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendOrder().execute(url);


    }

    private class SendOrder extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            String url= urls[0];

            JSONObject json = JSONParser.postToUrlObj(url, jObj, TopUpActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            topUpAmount = "0";
            topUpText.setText(topUpAmount);
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

