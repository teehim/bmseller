package seller.bmwallet.com.bangmodseller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Date;

import seller.bmwallet.com.bangmodseller.Class.SessionManager;
import seller.bmwallet.com.bangmodseller.Class.User;
import seller.bmwallet.com.bangmodseller.Class.Utility;


public class MainActivity extends Activity {

    ImageView toSell,toProduct,toPromotion,toReport;
    SessionManager ss;
    TextView storeName, dateTxt;
    DateTime now = DateTime.now();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storeName = (TextView) findViewById(R.id.store_name);
        dateTxt = (TextView) findViewById(R.id.dateTxt);

        ss = new SessionManager(this);

        String date = now.getDayOfMonth()+" "+ Utility.getMonthName(now.getMonthOfYear())+" "+(now.getYear()+543);
        dateTxt.setText(date);
        storeName.setText(ss.getStoreName());

        toSell = (ImageView) findViewById(R.id.to_sell);
        toProduct = (ImageView) findViewById(R.id.to_product);
        toPromotion = (ImageView) findViewById(R.id.to_promo);
        toReport = (ImageView) findViewById(R.id.to_report);

        toSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SellingActivity.class);
                startActivity(intent);
            }
        });
        toProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ManageProductActivity.class);
                startActivity(intent);
            }
        });
        toPromotion.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ManagePromotionActivity.class);
                startActivity(intent);
            }
        });
        toReport.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ReportActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(getBaseContext(), "You selected Phone", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logout:
                ss = new SessionManager(this);
                ss.logoutUser();
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
