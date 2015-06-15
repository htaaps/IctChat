package in.htlabs.tapas.ictchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private ArrayList<Users> userList = new ArrayList<Users>();

    ListView listview;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_main);
        new GetUsers().execute();
    }

    // Download all the users avilable for messaging
    private class GetUsers extends AsyncTask<String, String, String> {

        // Progress Dialog
        private ProgressDialog pDialog;

        // JSON parser class
        JSONParser jsonParser = new JSONParser();

        //testing from a real server:
        private static final String GETUSER_URL = "http://www.htlabs.in/student/ictchat/getuser.php";

        // JSON IDS:
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_NAME    = "name";
        private static final String TAG_USERNAME = "username";
        private static final String TAG_POST = "posts";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting all the users...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(GETUSER_URL, "POST", params);

                // json success tag
                success = json.getInt(TAG_SUCCESS);

                Log.d("the users are",json.toString());

                if (success == 1) {
                    JSONArray jusers = json.getJSONArray(TAG_POST);
                    for (int i = 0; i < jusers.length(); i++) {
                        JSONObject jobj = (JSONObject) jusers.get(i);
                        Users user= new Users(jobj.getString(TAG_NAME),jobj.getString(TAG_USERNAME));
                        userList.add(user);
                    }
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

        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(MainActivity.this, userList);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
        }
    }
}
