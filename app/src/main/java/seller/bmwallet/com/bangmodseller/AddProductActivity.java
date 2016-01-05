package seller.bmwallet.com.bangmodseller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import seller.bmwallet.com.bangmodseller.Class.JSONParser;
import seller.bmwallet.com.bangmodseller.Class.Product;
import seller.bmwallet.com.bangmodseller.Class.SessionManager;


public class AddProductActivity extends FragmentActivity implements NumberPickerDialogFragment.NumberPickerDialogHandler {

    int indi = 0;
    TextView price, point;
    Button add;
    DateTime d;
    EditText productName,productDetail;
    Product product = new Product();
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    JSONObject jObj;
    ImageView camera;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    String prodImage = "";
    SessionManager ss;
    private static final int PICK_IMAGE = 2;
    String imageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        ss = new SessionManager(this);

        productName = (EditText) findViewById(R.id.product_name);
        productDetail = (EditText) findViewById(R.id.detail);
        camera = (ImageView) findViewById(R.id.camera);



        point = (TextView) findViewById(R.id.point);
        price = (TextView) findViewById(R.id.price);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
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
        add = (Button) findViewById(R.id.addBtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setProductName(productName.getText().toString());
                product.setProductDetail(productDetail.getText().toString());
                jObj = new JSONObject();
                try {
                    jObj.put("product_name",product.getProductName());
                    jObj.put("product_detail",product.getProductDetail());
                    jObj.put("store_id",ss.getUserDetails().getUserId());
                    jObj.put("point",product.getPoint());
                    jObj.put("price",product.getPrice());
                    if(!TextUtils.isEmpty(prodImage)){
                        jObj.put("Image",prodImage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url[] = new String[1];
                url[0] = "https://apps.bm-wallet.com/store/add_product.php";
                new JSONPost().execute(url);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_product, menu);
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
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();

    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
        if(indi == 0){
            product.setPoint(number+"");
            point.setText(number+"");
        }else {
            product.setPrice(fullNumber);
            price.setText(fullNumber + "");
        }
    }

    private class JSONPost extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            String url= urls[0];

            // Getting JSON from URL
            JSONObject json = JSONParser.postToUrlObj(url, jObj, AddProductActivity.this);

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Toast.makeText(AddProductActivity.this, "finish", Toast.LENGTH_LONG).show();

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
}


