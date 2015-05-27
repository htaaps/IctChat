package in.htlabs.tapas.ictchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tapas on 5/15/2015.
 */
public class Message extends Activity implements View.OnClickListener{

    EditText et_msg;
    Button bt_send;
    String name;
    String username;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        et_msg=(EditText)findViewById(R.id.et_msg);
        bt_send=(Button)findViewById(R.id.bt_send);

        bt_send.setOnClickListener(this);

        Intent i = getIntent();

        name = i.getStringExtra("name");

        username = i.getStringExtra("username");

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_send:
                message=et_msg.getText().toString();
                new SendMessage().execute();
                break;
        }
    }
    class SendMessage extends AsyncTask<String, String, String> {

        // Progress Dialog
        private ProgressDialog pDialog;

        // JSON parser class
        JSONParser jsonParser = new JSONParser();

        //testing from a real server:
        private static final String SEND_MSG_URL = "http://www.htlabs.in/tapas/wealthcreator/sendmessage.php";

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
            pDialog = new ProgressDialog(Message.this);
            pDialog.setMessage("Sending the message...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("message", message));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(SEND_MSG_URL, "POST", params);

                // json success tag
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Log.d("Login Successful!", json.toString());
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
                Toast.makeText(Message.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

}
