package seller.bmwallet.com.bangmodseller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
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
import java.text.DecimalFormat;
import java.util.ArrayList;

import seller.bmwallet.com.bangmodseller.Class.AddProdArrayAdapter;
import seller.bmwallet.com.bangmodseller.Class.JSONParser;
import seller.bmwallet.com.bangmodseller.Class.Product;
import seller.bmwallet.com.bangmodseller.Class.SessionManager;


public class ManageProductActivity extends FragmentActivity implements NumberPickerDialogFragment.NumberPickerDialogHandler {

    ListView prodList;
    JSONObject jObj;
    ArrayList<Product> list = new ArrayList<Product>();
    int indi = 0;
    TextView price, point;
    Button posBtn,negBtn;
    DateTime d;
    EditText productName,productDetail;
    Product product = null;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    ImageView camera;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    String prodImage = "";
    private static final int PICK_IMAGE = 2;
    SessionManager ss;
    int action = 0,clickCount=0;
    String imageFilePath;
    String tempPrice,tempPoint;
    ImageLoaderConfiguration config;
    ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);
        prodList = (ListView) findViewById(R.id.prod_list);

        config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
        ss = new SessionManager(this);
        String url[] = new String[1];
        url[0] = "https://apps.bm-wallet.com/store/get_product_by_id.php";
        new GetProduct().execute(url);

        productName = (EditText) findViewById(R.id.product_name);
        productDetail = (EditText) findViewById(R.id.detail);
        camera = (ImageView) findViewById(R.id.camera);
        point = (TextView) findViewById(R.id.point);
        price = (TextView) findViewById(R.id.price);

        productName.setEnabled(false);
        productDetail.setEnabled(false);
        price.setEnabled(false);
        point.setEnabled(false);
        camera.setEnabled(false);

        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indi = 0;
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                npb.show();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {
                        "Take a Picture", "Choose from Gallery"
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductActivity.this);
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

        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indi = 1;
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                npb.show();
            }
        });

        posBtn = (Button) findViewById(R.id.posBtn);
        negBtn = (Button) findViewById(R.id.negBtn);
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCount == 0) {
                    if(product!=null){
                        action = 0;
                        productName.setEnabled(true);
                        camera.setEnabled(true);
                        productDetail.setEnabled(true);
                        price.setEnabled(true);
                        point.setEnabled(true);
                        posBtn.setText("CONFIRM");
                        negBtn.setText("CANCEL");
                        clickCount++;
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductActivity.this);
                        builder.setMessage("Please Select Product First.")
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
                    if(!TextUtils.isEmpty(productName.getText())&&!TextUtils.isEmpty(productDetail.getText())&&!TextUtils.isEmpty(price.getText())&&!TextUtils.isEmpty(point.getText())) {
                        try {
                            if (action == 1) {
                                product = new Product();
                                product.setProductName(productName.getText().toString());
                                product.setProductDetail(productDetail.getText().toString());
                                product.setPrice(Double.parseDouble(price.getText().toString()));
                                product.setPoint(point.getText().toString());
                                jObj.put("product_name", product.getProductName());
                                jObj.put("product_detail", product.getProductDetail());
                                jObj.put("point", product.getPoint());
                                jObj.put("price", product.getPrice());
                                if (!TextUtils.isEmpty(prodImage)) {
                                    jObj.put("image", prodImage);
                                }
                                url[0] = "https://apps.bm-wallet.com/store/add_product.php";
                            } else if (action == 0) {
                                jObj.put("product_id", product.getId());
                                url[0] = "https://apps.bm-wallet.com/store/update_product.php";
                                jObj.put("product_name", product.getProductName());
                                jObj.put("product_detail", product.getProductDetail());
                                jObj.put("point", tempPoint);
                                jObj.put("price", tempPrice);
                                jObj.put("type","U");
                                if (!TextUtils.isEmpty(prodImage)) {
                                    jObj.put("image", prodImage);
                                }
                            }
                            jObj.put("store_id", ss.getUserDetails().getUserId());


                            if (product.getPrice() != 0) {
                                if (!TextUtils.isEmpty(product.getProductName()) && !TextUtils.isEmpty(product.getProductDetail()) &&
                                        !TextUtils.isEmpty(price.getText().
                                                toString()) && !TextUtils.isEmpty(point.getText().toString())) {
                                    new JSONPost().execute(url);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductActivity.this);
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
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductActivity.this);
                                builder.setMessage("Price cannot be below 0.")
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductActivity.this);
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

        });

        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickCount==0){
                    clickCount++;
                    action=2;
                    if(product!=null){
                        jObj = new JSONObject();
                        final String url[] = new String[1];
                        url[0] = "https://apps.bm-wallet.com/store/update_product.php";
                        try {
                            jObj.put("product_id", product.getId());
                            jObj.put("store_id",ss.getUserDetails().getUserId());
                            jObj.put("type","D");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductActivity.this);
                        builder.setMessage("Are You Sure?")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new JSONPost().execute(url);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();


                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductActivity.this);
                        builder.setMessage("Please Select Product First.")
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
                    if(action==0){
                        productName.setText(product.getProductName());
                        productDetail.setText(product.getProductDetail());
                        price.setText(product.getPrice()+"");
                        point.setText(product.getPoint());
                        imageLoader.loadImage( "https://apps.bm-wallet.com/store/images/products/"+product.getProductImage(), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                                camera.setImageBitmap(loadedImage);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                loadedImage.compress(Bitmap.CompressFormat.PNG, 30, stream);
                                byte[] byte_arr = stream.toByteArray();
                                prodImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                            }
                        });

                    }else {
                        camera.setImageDrawable(getResources().getDrawable(R.drawable.camera));
                        productName.setText("");
                        productDetail.setText("");
                        price.setText("");
                        point.setText("");
                        camera.setEnabled(false);
                    }
                    productName.setEnabled(false);
                    productDetail.setEnabled(false);
                    price.setEnabled(false);
                    camera.setEnabled(false);
                    point.setEnabled(false);
                    posBtn.setText("EDIT");
                    negBtn.setText("REMOVE");
                    clickCount--;
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_product, menu);
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
                    addProductOption();
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetProduct extends AsyncTask<String, Void, JSONArray> {
        ProgressDialog progress = new ProgressDialog(ManageProductActivity.this);

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
            JSONArray json = JSONParser.postToUrlArr(url, jObj, ManageProductActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            list = new ArrayList<Product>();
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
            prodList.setAdapter(new AddProdArrayAdapter(ManageProductActivity.this, R.layout.fragment_prod_list, list, R.layout.prod_list));
        }

    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
        if(isNegative==false) {
            if (indi == 0) {
                tempPoint = number + "";
                point.setText(tempPoint);
            } else {
                DecimalFormat format = new DecimalFormat("0.00");
                tempPrice = format.format(fullNumber);
                price.setText(tempPrice);
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductActivity.this);
            builder.setMessage("Price and Point cannot be Negative.")
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

    private class JSONPost extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog progress = new ProgressDialog(ManageProductActivity.this);

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
                result = JSONParser.normalPost(url, nameValuePairs, ManageProductActivity.this);
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
                    if (clickCount == 1) {
                        if (action == 0) {
                            msg = "Edit Successful";
                        } else if (action == 1) {
                            msg = "Add Successful";
                        }else{
                            msg = "Remove Successful";
                        }
                    }
                    Toast.makeText(ManageProductActivity.this, msg, Toast.LENGTH_LONG).show();


                    if (action == 0) {

                        imageLoader.loadImage( "https://apps.bm-wallet.com/store/images/products/"+product.getProductImage(), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                                camera.setImageBitmap(loadedImage);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                loadedImage.compress(Bitmap.CompressFormat.PNG, 30, stream);
                                byte[] byte_arr = stream.toByteArray();
                                prodImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                            }
                        });

                    } else {
                        productName.setText("");
                        productDetail.setText("");
                        price.setText("");
                        point.setText("");
                        camera.setImageDrawable(getResources().getDrawable(R.drawable.camera));
                    }
                    price.setEnabled(false);
                    point.setEnabled(false);
                    productName.setEnabled(false);
                    productDetail.setEnabled(false);
                    camera.setEnabled(false);
                    posBtn.setText("EDIT");
                    negBtn.setText("REMOVE");
                    clickCount--;
                }else{
                    Toast.makeText(ManageProductActivity.this, "Action Fail", Toast.LENGTH_LONG).show();
                }
            String url[] = new String[1];
            url[0] = "https://apps.bm-wallet.com/store/get_product_by_id.php";
            new GetProduct().execute(url);

            progress.dismiss();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            camera.setImageBitmap(imageBitmap);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);

        }else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){


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
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        prodImage  = Base64.encodeToString(byte_arr, Base64.DEFAULT);
    }

    protected void fetchProductPicture(int option){
        if(option==0) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }else{
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, PICK_IMAGE);
        }
    }

    public void getProdDetail(View v) {

        final ListView promoList = (ListView) findViewById(R.id.prod_list);
        final int position = promoList.getPositionForView((LinearLayout) v);
        product = (Product) promoList.getAdapter().getItem(position);
        camera.setImageDrawable(getResources().getDrawable(R.drawable.camera));

        imageLoader.loadImage( "https://apps.bm-wallet.com/store/images/products/"+product.getProductImage(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                camera.setImageBitmap(loadedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                loadedImage.compress(Bitmap.CompressFormat.PNG, 30, stream);
                byte[] byte_arr = stream.toByteArray();
                prodImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            }
        });

        productName.setText(product.getProductName());
        productName.setEnabled(false);
        productDetail.setText(product.getProductDetail());
        productDetail.setEnabled(false);
        price.setText(product.getPrice()+"");
        price.setEnabled(false);
        point.setText(product.getPoint());
        point.setEnabled(false);
        tempPoint = product.getPoint();
        tempPrice = product.getPrice()+"";

        if(clickCount==1){
            clickCount--;
            posBtn.setText("EDIT");
            negBtn.setText("REMOVE");
        }
    }

    public void addProductOption(){
        clickCount++;
        productName.setText("");
        productDetail.setText("");
        price.setText("");
        camera.setImageDrawable(getResources().getDrawable(R.drawable.camera));
        point.setText("");
            camera.setEnabled(true);
            action = 1;
            productName.setEnabled(true);
            productDetail.setEnabled(true);
            price.setEnabled(true);
            point.setEnabled(true);
            posBtn.setText("ADD");
            negBtn.setText("CANCEL");

    }
}


