package seller.bmwallet.com.bangmodseller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import seller.bmwallet.com.bangmodseller.Class.AddProdArrayAdapter;
import seller.bmwallet.com.bangmodseller.Class.AddPromoArrayAdapter;
import seller.bmwallet.com.bangmodseller.Class.JSONParser;
import seller.bmwallet.com.bangmodseller.Class.Product;
import seller.bmwallet.com.bangmodseller.Class.Promotion;
import seller.bmwallet.com.bangmodseller.Class.Reward;
import seller.bmwallet.com.bangmodseller.Class.SessionManager;
import seller.bmwallet.com.bangmodseller.Class.Utility;


public class ManagePromotionActivity extends FragmentActivity implements CalendarDatePickerDialog.OnDateSetListener, NumberPickerDialogFragment.NumberPickerDialogHandler {

    private static final int REWARD_RESULT = 0;
    Button rewardBtn, posBtn, negBtn,getBtn;
    ArrayList<Reward> rewardList = new ArrayList<Reward>();
    ArrayList<Promotion> list;
    EditText title, description;
    TextView strDate, expDate, usePoint;
    DateTime now = DateTime.now(),str=null,exp=null;
    Promotion promotion = null,newPromotion = new Promotion();
    ListView promoList;
    int mode = 0;
    int indi = 0;
    SessionManager ss;
    JSONObject jObj;
    String prodImage = "";
    ImageView camera;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    ImageLoaderConfiguration config;
    ImageLoader imageLoader;
    int lastAction = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_promotion);

        config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();

        ss = new SessionManager(this);
        promoList = (ListView) findViewById(R.id.promo_list);

                String url[] = new String[1];
                url[0] = "https://apps.bm-wallet.com/store/get_promotion_by_store_id.php";
                new GetPromo().execute(url);



        camera = (ImageView) findViewById(R.id.camera);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        strDate = (TextView) findViewById(R.id.str_date);
        expDate = (TextView) findViewById(R.id.exp_date);
        usePoint = (TextView) findViewById(R.id.use_point);
        posBtn = (Button) findViewById(R.id.posBtn);
        negBtn = (Button) findViewById(R.id.negBtn);

        title.setEnabled(false);
        description.setEnabled(false);
        strDate.setEnabled(false);
        expDate.setEnabled(false);
        usePoint.setEnabled(false);
        camera.setEnabled(false);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {
                        "Take a Picture", "Choose from Gallery"
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(ManagePromotionActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        fetchProductPicture(item);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        rewardBtn = (Button) findViewById(R.id.rewardBtn);
        rewardBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent rewardIntent = new Intent(ManagePromotionActivity.this, RewardListDialogActivity.class);
                if(promotion!=null) {
                    rewardList = promotion.getPromoSet();
                }
                rewardIntent.putExtra("rewardList", rewardList);
                rewardIntent.putExtra("mode", mode);
                startActivityForResult(rewardIntent, REWARD_RESULT);
            }
        });

        usePoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                npb.show();
            }
        });

        strDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indi = 0;
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(ManagePromotionActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        expDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indi = 1;
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(ManagePromotionActivity.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        posBtn.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if (mode == 0) {
                                              if (promotion != null) {
                                                  mode = 1;
                                                  title.setEnabled(true);
                                                  description.setEnabled(true);
                                                  strDate.setEnabled(true);
                                                  expDate.setEnabled(true);
                                                  usePoint.setEnabled(true);
                                                  camera.setEnabled(true);
                                                  rewardBtn.setText("Add Reward Set");
                                                  posBtn.setText("CONFIRM");
                                                  negBtn.setText("CANCEL");
                                              } else {
                                                  AlertDialog.Builder builder = new AlertDialog.Builder(ManagePromotionActivity.this);
                                                  builder.setMessage("Please Select Promotion First.")
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
                                          } else {
                                              String url[] = new String[1];
                                              jObj = new JSONObject();
                                              JSONArray rewArr = new JSONArray();
                                              JSONObject rewObj;
                                              for(int i=0;i<rewardList.size();i++){
                                                rewObj = new JSONObject();
                                                  try {
                                                      rewObj.put("product_id",rewardList.get(i).getProductId());
                                                      rewObj.put("amount",rewardList.get(i).getAmount());
                                                      rewObj.put("price",rewardList.get(i).getPrice());

                                                      rewArr.put(rewObj);
                                                  } catch (JSONException e) {
                                                      e.printStackTrace();
                                                  }
                                              }


                                              newPromotion.setTitle(title.getText().toString());
                                              newPromotion.setDescription(description.getText().toString());
                                              newPromotion.setStrDate(strDate.getText().toString());
                                              newPromotion.setExpDate(expDate.getText().toString());
                                              newPromotion.setUsePoint(usePoint.getText().toString());
                                              try {
                                                  jObj.put("title", newPromotion.getTitle());
                                                  jObj.put("start_date", newPromotion.getStrDate());
                                                  jObj.put("description", newPromotion.getDescription());
                                                  jObj.put("expire_date", newPromotion.getExpDate());
                                                  jObj.put("use_point",newPromotion.getUsePoint());
                                                  jObj.put("store_id", ss.getUserDetails().getUserId());
                                                  jObj.put("type","U");
                                                  jObj.put("reward_list",rewArr);
                                                  if (!TextUtils.isEmpty(prodImage)) {
                                                      jObj.put("image", prodImage);
                                                  }
                                              } catch (JSONException e) {
                                                  e.printStackTrace();
                                              }
                                              if (mode == 1) {
                                                  try {
                                                      jObj.put("promotionid", promotion.getProId());
                                                  } catch (JSONException e) {
                                                      e.printStackTrace();
                                                  }
                                                  url[0] = "https://apps.bm-wallet.com/store/update_promotion.php";
                                              } else if(mode == 2){
                                                  url[0] = "https://apps.bm-wallet.com/store/add_promotion.php";

                                              }


                                              if (!TextUtils.isEmpty(newPromotion.getTitle()) && !TextUtils.isEmpty(newPromotion.getDescription()) &&
                                                      !TextUtils.isEmpty(newPromotion.getStrDate()) && !TextUtils.isEmpty(newPromotion.getExpDate()) && !TextUtils.isEmpty(newPromotion.getUsePoint())) {
                                                  new JSONPost().execute(url);
                                              } else {
                                                  AlertDialog.Builder builder = new AlertDialog.Builder(ManagePromotionActivity.this);
                                                  builder.setMessage("Please Insert all the Information.")
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
                                          }
                                      }

                                  }

        );

        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode==0){
                    if(promotion!=null){
                        jObj = new JSONObject();
                        final String url[] = new String[1];
                        url[0] = "https://apps.bm-wallet.com/store/update_promotion.php";
                        try {
                            jObj.put("promotionid",promotion.getProId());
                            jObj.put("store_id",promotion.getStoreId());
                            jObj.put("type","D");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManagePromotionActivity.this);
                        builder.setMessage("Are You Sure?")
                                .setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new JSONPost().execute(url);
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManagePromotionActivity.this);
                        builder.setMessage("Please Choose Promotion First.")
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
                    if(mode==1){
                        title.setText(promotion.getTitle());
                        description.setText(promotion.getDescription());
                        strDate.setText(promotion.getStrDate());
                        expDate.setText(promotion.getExpDate());
                        usePoint.setText(promotion.getUsePoint());
                        imageLoader.loadImage( "https://apps.bm-wallet.com/store/images/promotions/"+promotion.getProIm(), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                                camera.setImageBitmap(loadedImage);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                loadedImage.compress(Bitmap.CompressFormat.PNG, 30, stream);
                                byte[] byte_arr = stream.toByteArray();
                                prodImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                            }
                        });


                    }else if(mode==2){
                        title.setText("");
                        description.setText("");
                        strDate.setText("");
                        expDate.setText("");
                        usePoint.setText("");
                        camera.setImageDrawable(getResources().getDrawable(R.drawable.camera));

                    }
                    title.setEnabled(false);
                    description.setEnabled(false);
                    strDate.setEnabled(false);
                    expDate.setEnabled(false);
                    usePoint.setEnabled(false);
                    camera.setEnabled(false);

                    mode = 0;
                    posBtn.setText("EDIT");
                    negBtn.setText("REMOVE");
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_promotion, menu);
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
        }else if(id == R.id.addBtn){
            addPromoOption();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getPromoDetail(View v) {
        final ListView promoList = (ListView) findViewById(R.id.promo_list);
        final int position = promoList.getPositionForView((LinearLayout) v);
        lastAction = position;
        Promotion listPromotion = (Promotion) promoList.getAdapter().getItem(position);
        promotion = new Promotion();
        promotion.setUsePoint(listPromotion.getUsePoint());
        promotion.setProIm(listPromotion.getProIm());
        promotion.setExpDate(listPromotion.getExpDate());
        promotion.setStrDate(listPromotion.getStrDate());
        promotion.setProId(listPromotion.getProId());
        promotion.setStoreId(listPromotion.getStoreId());
        promotion.setStoreName(listPromotion.getStoreName());
        promotion.setDescription(listPromotion.getDescription());
        promotion.setTitle(listPromotion.getTitle());
        String url[] = new String[1];
        url[0] = "https://apps.bm-wallet.com/store/get_promotion_detail.php";
        new GetReward().execute(url);

        title.setText(promotion.getTitle());
        description.setText(promotion.getDescription());
        strDate.setText(promotion.getStrDate());
        expDate.setText(promotion.getExpDate());
        usePoint.setText(promotion.getUsePoint());
        camera.setImageDrawable(getResources().getDrawable(R.drawable.camera));

        imageLoader.loadImage( "https://apps.bm-wallet.com/store/images/promotions/"+promotion.getProIm(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                camera.setImageBitmap(loadedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                loadedImage.compress(Bitmap.CompressFormat.PNG, 30, stream);
                byte[] byte_arr = stream.toByteArray();
                prodImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            }
        });
        if (mode != 0) {
            title.setEnabled(false);
            description.setEnabled(false);
            strDate.setEnabled(false);
            expDate.setEnabled(false);
            usePoint.setEnabled(false);
            camera.setEnabled(false);
            rewardBtn.setText("Show Reward Set");
            mode=0;
            posBtn.setText("EDIT");
            negBtn.setText("REMOVE");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap imageBitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        switch (requestCode) {
            case (REWARD_RESULT): {
                if (resultCode == Activity.RESULT_OK) {
                    rewardList = (ArrayList<Reward>) data.getExtras().getSerializable("rewardList");
                    if(promotion==null){
                        promotion = new Promotion();
                    }
                    promotion.setPromoSet(rewardList);
                }
                break;

            }
            case (REQUEST_IMAGE_CAPTURE): {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    camera.setImageBitmap(imageBitmap);
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
                    byte[] byte_arr = stream.toByteArray();
                    prodImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                }
            }
            break;

            case (PICK_IMAGE): {
                if (resultCode == RESULT_OK) {


                    Uri selectedImage = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                        // Decode image size
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);
                        final int REQUIRED_SIZE = 140;

                        // Find the correct scale value. It should be the power of 2.
                        int width_tmp = o.outWidth, height_tmp = o.outHeight;
                        int scale = 1;
                        while (true) {
                            if (width_tmp / 2 < REQUIRED_SIZE
                                    || height_tmp / 2 < REQUIRED_SIZE) {
                                break;
                            }
                            width_tmp /= 2;
                            height_tmp /= 2;
                            scale *= 2;
                        }

                        BitmapFactory.Options o2 = new BitmapFactory.Options();
                        o2.inSampleSize = scale;

                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
                        camera.setImageBitmap(imageBitmap);
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
                        byte[] byte_arr = stream.toByteArray();
                        prodImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } break;
        }
    }

    protected void fetchProductPicture(int option) {
        if (option == 0) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, PICK_IMAGE);
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        if(indi == 0) {
            if((now.getYear()==year&&now.getMonthOfYear()==monthOfYear+1&&now.getDayOfMonth()<=dayOfMonth)||(now.getYear()==year&&now.getMonthOfYear()<monthOfYear+1)||(now.getYear()<year)) {
                if(exp!=null) {
                    if((exp.getYear()==year&&exp.getMonthOfYear()==monthOfYear+1&&exp.getDayOfMonth()>=dayOfMonth)||(exp.getYear()==year&&exp.getMonthOfYear()>monthOfYear+1)||(exp.getYear()>year)) {
                        str = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                        String sDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                        strDate.setText(sDate);
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManagePromotionActivity.this);
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
                    String sDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                    strDate.setText(sDate);
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ManagePromotionActivity.this);
                builder.setMessage("Start Date cannot be before Present Day.")
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
        }else {
            if (str != null) {
                if ((str.getYear() == year && str.getMonthOfYear() == monthOfYear+1 && str.getDayOfMonth() <= dayOfMonth) || (str.getYear() == year && str.getMonthOfYear() < monthOfYear+1) || (str.getYear() < year)) {
                    String sDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                    exp = new DateTime(year,monthOfYear+1,dayOfMonth,0,0);
                    expDate.setText(sDate);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManagePromotionActivity.this);
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
                if((now.getYear()==year&&now.getMonthOfYear()==monthOfYear+1&&now.getDayOfMonth()<=dayOfMonth)||(now.getYear()==year&&now.getMonthOfYear()<monthOfYear+1)||(now.getYear()<year)) {
                    String sDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                    exp = new DateTime(year,monthOfYear+1,dayOfMonth,0,0);
                    expDate.setText(sDate);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManagePromotionActivity.this);
                    builder.setMessage("Expire Date have to be after Present Day.")
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
            }
        }
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
            usePoint.setText(number+"");
    }

    public void addPromoOption(){
        mode = 2;

        title.setText("");
        description.setText("");
        strDate.setText("");
        expDate.setText("");
        usePoint.setText("");
        camera.setImageDrawable(getResources().getDrawable(R.drawable.camera));
        rewardBtn.setText("Add Reward Set");

        title.setEnabled(true);
        description.setEnabled(true);
        strDate.setEnabled(true);
        expDate.setEnabled(true);
        usePoint.setEnabled(true);
        camera.setEnabled(true);

        promotion = null;
        rewardList = new ArrayList<Reward>();

        posBtn.setText("ADD");
        negBtn.setText("CANCEL");

    }

    private class JSONPost extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog progress = new ProgressDialog(ManagePromotionActivity.this);

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            progress.setMessage("Working...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            String url= urls[0];
            JSONObject result = new JSONObject();

            // Getting JSON from URL
            ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

            String json = jObj.toString();

            nameValuePairs.add(new BasicNameValuePair("json",json));
            progress.show();
            try {
                result = JSONParser.normalPost(url, nameValuePairs, ManagePromotionActivity.this);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            String status = "";
            try {
                status = json.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
                if(status.equals("1")) {
                    String msg = "";
                    if (mode == 0) {
                        msg = "Remove Successful";
                    } else if (mode == 1) {
                        msg = "Edit Successful";
                    } else {
                        msg = "Add Successful";
                    }
                    Toast.makeText(ManagePromotionActivity.this, msg, Toast.LENGTH_LONG).show();


                    if (mode == 1) {

                        imageLoader.loadImage( "https://apps.bm-wallet.com/store/images/promotions/"+promotion.getProIm(), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                                camera.setImageBitmap(loadedImage);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                loadedImage.compress(Bitmap.CompressFormat.PNG, 30, stream);
                                byte[] byte_arr = stream.toByteArray();
                                prodImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                            }
                        });

                    } else if (mode == 2) {
                        title.setText("");
                        description.setText("");
                        strDate.setText("");
                        expDate.setText("");
                        usePoint.setText("");
                        camera.setImageDrawable(getResources().getDrawable(R.drawable.camera));

                    }
                    title.setEnabled(false);
                    description.setEnabled(false);
                    strDate.setEnabled(false);
                    expDate.setEnabled(false);
                    usePoint.setEnabled(false);
                    camera.setEnabled(false);

                    mode = 0;
                    posBtn.setText("EDIT");
                    negBtn.setText("REMOVE");
                }else{
                    Toast.makeText(ManagePromotionActivity.this, "Action Fail", Toast.LENGTH_LONG).show();
                }

            String url[] = new String[1];
            url[0] = "https://apps.bm-wallet.com/store/get_promotion_by_store_id.php";
            new GetPromo().execute(url);
            progress.dismiss();
        }

    }

    private class GetPromo extends AsyncTask<String, Void, JSONArray> {

        ProgressDialog progress = new ProgressDialog(ManagePromotionActivity.this);

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.show();
            JSONArray json = JSONParser.postToUrlArr(url, jObj, ManagePromotionActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            list = new ArrayList<Promotion>();
            for(int i=json.length()-1;i>=0;i--){
                Promotion p = new Promotion();
                try {
                    p.setTitle(json.getJSONObject(i).getString("TITLE"));
                    p.setProId(json.getJSONObject(i).getString("PROMOTIONID"));
                    p.setDescription(json.getJSONObject(i).getString("DESCRIPTION"));
                    p.setStrDate(json.getJSONObject(i).getString("STARTDATE"));
                    p.setExpDate(json.getJSONObject(i).getString("EXPIREDATE"));
                    p.setUsePoint(json.getJSONObject(i).getString("USEMEMBERPOINT"));
                    p.setProIm(json.getJSONObject(i).getString("PHOTO"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(p);
            }
            progress.dismiss();
            promoList.setAdapter(new AddPromoArrayAdapter(ManagePromotionActivity.this, R.layout.fragment_promo_list, list, R.layout.promo_list));
        }

    }

    private class GetReward extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... urls) {
            String url= urls[0];
            jObj = new JSONObject();



            try {
                jObj.put("promo_id",promotion.getProId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Getting JSON from URL
            JSONArray json = JSONParser.postToUrlArr(url, jObj, ManagePromotionActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            ArrayList<Reward> arReward = new ArrayList<Reward>();
            for(int i=0;i<json.length();i++){
                Reward promoSet = new Reward();
                try {
                    promoSet.setProductImage(json.getJSONObject(i).getString("IMAGEPATH"));
                    promoSet.setPrice(json.getJSONObject(i).getString("PRICE"));
                    promoSet.setProductId(json.getJSONObject(i).getString("PRODUCTID"));
                    promoSet.setAmount(json.getJSONObject(i).getString("AMOUNT"));
                    promoSet.setProductName(json.getJSONObject(i).getString("NAME"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arReward.add(promoSet);
            }
            promotion.setPromoSet(arReward);


        }

    }



}
