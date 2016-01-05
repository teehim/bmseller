package seller.bmwallet.com.bangmodseller.Class;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import seller.bmwallet.com.bangmodseller.LoginActivity;

/**
 * Created by Thanatkorn on 9/28/2014.
 */
public class SessionManager {

    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserData";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    ArrayList<Promotion> promotionList;
    JSONObject jObj;
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(JSONObject info){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        JSONObject user = null;

        try {
            user = new JSONObject(info.getString("SellerInfo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        promotionList = new ArrayList<Promotion>();

        try {


            editor.putString("userId", user.getString("SELLERID"));
            editor.putString("firstName", user.getString("FIRSTNAME"));
            editor.putString("lastName", user.getString("LASTNAME"));
            editor.putString("storeName", user.getString("STORENAME"));

        } catch (JSONException e) {
            System.out.println("Fail here");
            e.printStackTrace();
        }
        // commit changes
        editor.commit();
    }



    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */

    public String getStoreName(){
        return pref.getString("storeName","Unknown");
    }

    public User getUserDetails(){
        User user = new User();
        user.setUserId(pref.getString("userId", "0"));
        user.setFirstName(pref.getString("firstName", "Firstname"));
        user.setLastName(pref.getString("lastName", "Lastname"));

        return user;
    }








    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();


        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

}
