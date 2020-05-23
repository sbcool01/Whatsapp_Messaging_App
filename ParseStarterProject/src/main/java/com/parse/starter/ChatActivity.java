package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    String activeUser="";

    ArrayList<String> messages=new ArrayList<String>();

    ArrayAdapter<String> arrayAdapter;

    ListView chatListView;
    private void scrollMyListViewToBottom() {
        chatListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatListView.setSelection(arrayAdapter.getCount() - 1);
            }
        });
    }

    public void sendChat(View view){
        final EditText chatEditText=(EditText)findViewById(R.id.chatEditText);
        ParseObject message=new ParseObject("Message");

        final String mes=chatEditText.getText().toString();
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipent", activeUser);
        message.put("message", mes);

        chatEditText.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    messages.add(mes);
                    arrayAdapter.notifyDataSetChanged();
                    scrollMyListViewToBottom();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent=getIntent();

        activeUser=intent.getStringExtra("username");
        setTitle(activeUser);

        chatListView=(ListView)findViewById(R.id.chatListView);
        arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);
        chatListView.setAdapter(arrayAdapter);

        ParseQuery<ParseObject> query1=new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipent", activeUser);

        ParseQuery<ParseObject> query2=new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("recipent", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", activeUser);

        List<ParseQuery<ParseObject>> queries=new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query=ParseQuery.or(queries);

        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        messages.clear();
                        for(ParseObject message:objects){
                            String msg=message.getString("message");
                            if(message.getString("recipent").equals(ParseUser.getCurrentUser().getUsername())){
                                msg=">"+msg;
                            }

                            messages.add(msg);
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        scrollMyListViewToBottom();

    }

}
