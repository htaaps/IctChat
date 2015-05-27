package in.htlabs.tapas.ictchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Tapas on 4/10/2015.
 */
public class Register extends Activity implements  View.OnClickListener, View.OnTouchListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "GCMRelated";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regid;


    //Views declaration
    EditText re_et_email,re_et_pass1,re_et_pass2,re_et_name,re_et_mobile;
    Button re_bt_register,re_bt_login;

    //Data entry variables
    String username,password,repassword,name,mobile;

    //Validation variable
    Boolean validate=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wc_register);

        //initializing all the views
        re_et_email=(EditText)findViewById(R.id.re_et_email);
        re_et_pass1=(EditText)findViewById(R.id.re_et_pass1);
        re_et_pass2=(EditText)findViewById(R.id.re_et_pass2);
        re_et_name=(EditText)findViewById(R.id.re_et_name);
        re_et_mobile=(EditText)findViewById(R.id.re_et_mobile);
        re_bt_register=(Button)findViewById(R.id.re_bt_register);
        re_bt_login=(Button)findViewById(R.id.re_bt_login);

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            regid = getRegistrationId(getApplicationContext());
            if(!regid.isEmpty()){
                re_bt_register.setEnabled(false);
            }else{
                re_bt_register.setEnabled(true);
            }
        }

        //adding click listener to the views
        re_bt_register.setOnTouchListener(this);
        re_bt_login.setOnClickListener(this);
    }

    public boolean onTouch(View v,MotionEvent me){
        switch(v.getId()){
            case R.id.re_bt_register:
                if(me.getAction()== MotionEvent.ACTION_DOWN){
                    validate=checkInput();
                }
                if(me.getAction()== MotionEvent.ACTION_UP){

                    // Check device for Play Services APK.
                    if (checkPlayServices()) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                        regid = getRegistrationId(getApplicationContext());

                        if (regid.isEmpty()&& validate) {
                            re_bt_register.setEnabled(false);
                            new RegisterUser(getApplicationContext(), gcm, getAppVersion(getApplicationContext())).execute();
                        }else{
                            Toast.makeText(getApplicationContext(), "Device already Registered", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.i(TAG, "No valid Google Play Services APK found.");
                    }

                }
                break;
        }
        return false;
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.re_bt_login:
                Intent i=new Intent(Register.this,Login.class);
                startActivity(i);
                finish();
                break;
        }
    }

    //Method to check all the inputs by the user is correct
    public boolean checkInput(){
        username=re_et_email.getText().toString().toLowerCase();
        password=re_et_pass1.getText().toString();
        repassword=re_et_pass2.getText().toString();
        name=re_et_name.getText().toString().toLowerCase();
        mobile=re_et_mobile.getText().toString();

        //username must have @
        if(!username.contains("@")){
            re_et_email.setText("");
            re_et_email.requestFocus();
            Toast.makeText(getApplicationContext(), "please enter correct email", Toast.LENGTH_SHORT).show();
            return false;
        }

        //password greater than 6 chars
        if(password.length()<6){
            re_et_pass1.setText("");
            re_et_pass2.setText("");
            re_et_pass1.requestFocus();
            Toast.makeText(getApplicationContext(), "please enter password greater than 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        //repassword greater than 6 characters
        if(repassword.length()<6){
            re_et_pass2.setText("");
            re_et_pass2.setText("");
            re_et_pass2.requestFocus();
            Toast.makeText(getApplicationContext(), "please enter password greater than 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        //mobile no should be of 10 digits
        if(mobile.length()>10 || mobile.length()<10){
            re_et_mobile.setText("");
            re_et_mobile.requestFocus();
            Toast.makeText(getApplicationContext(), "please enter mobile number of 10 digits", Toast.LENGTH_SHORT).show();
            return false;
         }

        //password matching
        if(!password.equals(repassword)){
            Toast.makeText(getApplicationContext(), "password do not match", Toast.LENGTH_SHORT).show();
            re_et_pass1.setText("");
            re_et_pass2.setText("");
            re_et_pass1.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(getApplicationContext());
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }



    class RegisterUser extends AsyncTask<String, String, String> {

        private static final String TAG = "GCMRelated";
        Context ctx;
        GoogleCloudMessaging gcm;
        String SENDER_ID = "127772045435";
        String regid = null;
        private int appVersion;
        public RegisterUser(Context ctx, GoogleCloudMessaging gcm, int appVersion){
            this.ctx = ctx;
            this.gcm = gcm;
            this.appVersion = appVersion;
        }

        // Progress Dialog
        private ProgressDialog pDialog;

        // JSON parser class
        JSONParser jsonParser = new JSONParser();

        //testing from a real server:
        //private static final String REGISTER_URL = "http://www.yourdomain.com/webservice/login.php";
        private static final String REGISTER_URL = "http://www.htlabs.in/tapas/wealthcreator/register.php";

        // JSON IDS:
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        /**
         * Before starting background thread Show Progress Dialog
         * */

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Registering user to server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String msg = "";

            try {

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(ctx);
                    }
                    regid = gcm.register(SENDER_ID);

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(ctx, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }


                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("name", name));
                params.add(new BasicNameValuePair("mobile", mobile));
                params.add(new BasicNameValuePair("regid", ""+regid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(REGISTER_URL, "POST", params);

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login Successful!", json.toString());
                    Intent i = new Intent(Register.this,Login.class);
                    startActivity(i);
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Register.this, file_url, Toast.LENGTH_LONG).show();
            }
        }

        private void storeRegistrationId(Context ctx, String regid) {
            final SharedPreferences prefs = ctx.getSharedPreferences(MainActivity.class.getSimpleName(),
                    Context.MODE_PRIVATE);
            Log.i(TAG, "Saving regId on app version " + appVersion);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("registration_id", regid);
            editor.putInt("appVersion", appVersion);
            editor.commit();
        }
    }
}
