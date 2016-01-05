package seller.bmwallet.com.bangmodseller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import seller.bmwallet.com.bangmodseller.Class.AddProdArrayAdapter;
import seller.bmwallet.com.bangmodseller.Class.JSONParser;
import seller.bmwallet.com.bangmodseller.Class.Product;
import seller.bmwallet.com.bangmodseller.Class.Report;
import seller.bmwallet.com.bangmodseller.Class.ReportArrayAdapter;
import seller.bmwallet.com.bangmodseller.Class.SessionManager;


public class ReportActivity extends FragmentActivity implements CalendarDatePickerDialog.OnDateSetListener {

    TextView strDate,endDate;
    Button submitBtn,repProd,repDaily,repMonthly;
    int indi,type;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    DateTime str=null,end=null;
    JSONObject jObj;
    SessionManager ss;
    ListView reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ss = new SessionManager(this);

        reportList = (ListView) findViewById(R.id.report_list);
        strDate = (TextView) findViewById(R.id.str_date);
        endDate = (TextView) findViewById(R.id.end_date);
        repProd = (Button) findViewById(R.id.rep_prod);
        repDaily = (Button) findViewById(R.id.rep_daily);
        repMonthly = (Button) findViewById(R.id.rep_monthly);
        submitBtn = (Button) findViewById(R.id.submitBtn);

        repProd.setBackgroundColor(0xff2fa7fc);
        repProd.setTextColor(Color.WHITE);
        type = 0;

        strDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indi = 0;
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(ReportActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indi = 1;
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(ReportActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if((!TextUtils.isEmpty(strDate.getText().toString())&&!TextUtils.isEmpty(endDate.getText().toString()))||type==3){
                    String url[] = new String[1];
                    url[0] = "https://apps.bm-wallet.com/store/gen_report_by_item.php";
                    new GetReport().execute(url);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
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

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        if(indi == 0) {

                if(end!=null) {
                    if((end.getYear()==year&&end.getMonthOfYear()==monthOfYear+1&&end.getDayOfMonth()>=dayOfMonth)||(end.getYear()==year&&end.getMonthOfYear()>monthOfYear+1)||(end.getYear()>year)) {
                        str = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                        String sDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        strDate.setText(sDate);
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                        builder.setMessage("Start Date have to be before Expire Date.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }else{
                    str = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                    String sDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    strDate.setText(sDate);
                }

        }else {
            if (str != null) {
                if ((str.getYear() == year && str.getMonthOfYear() == monthOfYear+1 && str.getDayOfMonth() <= dayOfMonth) || (str.getYear() == year && str.getMonthOfYear() < monthOfYear+1) || (str.getYear() < year)) {
                    String sDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    end = new DateTime(year,monthOfYear+1,dayOfMonth,0,0);
                    endDate.setText(sDate);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                    builder.setMessage("Expire Date have to be after Start Date.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }else{

                    String sDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    end = new DateTime(year,monthOfYear+1,dayOfMonth,0,0);
                    endDate.setText(sDate);

            }
        }
    }
    private class GetReport extends AsyncTask<String, Void, JSONArray> {
        ProgressDialog progress = new ProgressDialog(ReportActivity.this);

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
            try {
                jObj = new JSONObject();
                jObj.put("store_id",ss.getUserDetails().getUserId());
                jObj.put("type",type);
                jObj.put("startdate",strDate.getText().toString());
                jObj.put("enddate",endDate.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.show();
            JSONArray json = JSONParser.postToUrlArr(url, jObj, ReportActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            ArrayList<Report> repList = new ArrayList<Report>();

            for(int i=0;i<json.length();i++){
                Report rep = new Report();
                try {
                    rep.setName(json.getJSONObject(i).getString("NAME"));
                    rep.setTotal(json.getJSONObject(i).getString("TOTALPRICE"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                repList.add(rep);
            }

            reportList.setAdapter(new ReportArrayAdapter(ReportActivity.this, R.layout.activity_report, repList, R.layout.report_list));

            progress.dismiss();
        }

    }

    public void selectReport(View v){
        System.out.println("Click");
        repProd.setBackgroundColor(Color.WHITE);
        repDaily.setBackgroundColor(Color.WHITE);
        repMonthly.setBackgroundColor(Color.WHITE);
        repProd.setTextColor(Color.BLACK);
        repDaily.setTextColor(Color.BLACK);
        repMonthly.setTextColor(Color.BLACK);
        switch (v.getId()){
            case R.id.rep_prod:
                repProd.setBackgroundColor(0xff2fa7fc);
                repProd.setTextColor(Color.WHITE);
                strDate.setEnabled(true);
                endDate.setEnabled(true);
                type=0;
                break;
            case R.id.rep_daily:
                repDaily.setBackgroundColor(0xff2fa7fc);
                repDaily.setTextColor(Color.WHITE);
                strDate.setEnabled(true);
                endDate.setEnabled(true);
                type=2;
                break;
            case R.id.rep_monthly:
                repMonthly.setBackgroundColor(0xff2fa7fc);
                repMonthly.setTextColor(Color.WHITE);
                strDate.setEnabled(false);
                endDate.setEnabled(false);
                type=3;
                break;
        }
    }
}
