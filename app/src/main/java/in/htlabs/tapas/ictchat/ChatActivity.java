package in.htlabs.tapas.ictchat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

/**
 * Created by Tapas on 5/20/2015.
 */
public class ChatActivity extends Activity {

    EditText chat_msg;
    Button send_btn;
    TableLayout tab;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tab = (TableLayout) findViewById(R.id.tab);

    }
}
