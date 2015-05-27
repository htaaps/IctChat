package in.htlabs.tapas.ictchat;

/**
 * Created by Tapas on 4/25/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    private ArrayList<Users> userList = new ArrayList<Users>();
    Users user = new Users();

    public ListViewAdapter(Context context, ArrayList<Users> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView tv_name;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
        user = userList.get(position);

        // Locate the TextViews in listview_item.xml
        tv_name = (TextView) itemView.findViewById(R.id.tv_name);

        // Capture position and set results to the TextViews
        tv_name.setText(user.getUser());

        // Capture ListView item click
        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                user = userList.get(position);
                Intent intent = new Intent(context, Message.class);
                // Pass all data rank
                intent.putExtra("username", user.getUserName());
                // Pass all data country
                intent.putExtra("name", user.getUser());

                context.startActivity(intent);

            }
        });
        return itemView;
    }
}