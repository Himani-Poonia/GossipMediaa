package com.example.gossipmediaa;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GossipFeed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gossip_feed);

        setTitle("Your Feed");

        final ListView listView = findViewById(R.id.listView);
        final List<Map<String,String>> feedsList = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Gossip");
        query.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));
        query.orderByDescending("createdAt");
        query.setLimit(20);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0) {
                    for(ParseObject gossip: objects){
                        Map<String,String> gossipInfo = new HashMap<>();   //make a map for every item
                        gossipInfo.put("content",gossip.getString("gossip")) ;
                        gossipInfo.put("username",gossip.getString("username"));
                        feedsList.add(gossipInfo);   //add map to list
                    }

                    SimpleAdapter simpleAdapter = new SimpleAdapter(com.example.gossipmediaa.GossipFeed.this,feedsList, android.R.layout.simple_list_item_2,new String[]{"username","content"},new int[]{android.R.id.text1,android.R.id.text2});
                    listView.setAdapter(simpleAdapter);
                }
            }
        });
    }
}