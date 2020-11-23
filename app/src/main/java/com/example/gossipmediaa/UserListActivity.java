package com.example.gossipmediaa;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter adapter;

//Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.user_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.newGossip){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Create New Gossip");
            EditText gossipEditText = new EditText(this);
            builder.setView(gossipEditText);

            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Log.i("Created",gossipEditText.getText().toString());

                    ParseObject gossip = new ParseObject("Gossip");
                    gossip.put("gossip",gossipEditText.getText().toString());
                    gossip.put("username",ParseUser.getCurrentUser().getUsername());

                    gossip.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Toast.makeText(com.example.gossipmediaa.UserListActivity.this, "Gossip Sent!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(com.example.gossipmediaa.UserListActivity.this, "Gossip failed :(", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Log.i("Canceled","No");
                    dialog.cancel();
                }
            });

            builder.show();
        }else if(item.getItemId() == R.id.gossipFeed) {

            Intent intent = new Intent(getApplicationContext(),GossipFeed.class);
            startActivity(intent);

        } else if(item.getItemId() == R.id.logout) {
                ParseUser.logOut();

                Intent intent = new Intent(getApplicationContext(), com.example.gossipmediaa.MainActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

//on create activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("Follow");

        ListView listView = findViewById(R.id.listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked,users);
        listView.setAdapter(adapter);

        //checks/unchecks list item
        listView.setOnItemClickListener((parent, view, position, id) -> {
            CheckedTextView checkedTextView = (CheckedTextView) view;

            if(checkedTextView.isChecked()) {
                ParseUser.getCurrentUser().add("isFollowing",users.get(position));
            } else {
//                    Log.i("Info","NOT Checked");
                ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(position));   //get the list of particular column
                List tempUser = ParseUser.getCurrentUser().getList("isFollowing");       //download the list with removed item into tempUser
                ParseUser.getCurrentUser().remove("isFollowing");                        //wipe the whole column once (this is some kind of bug or something in parse)
                ParseUser.getCurrentUser().put("isFollowing",tempUser);                     //now add the whole column with one removed item
            }

            ParseUser.getCurrentUser().saveInBackground();
        });

        //check item if that particular item is present in current users isFollowing list in parse
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());

        query.findInBackground((objects, e) -> {
            if(e == null && objects.size() > 0) {
                for(ParseUser user : objects) {
                    users.add(user.getUsername());
                }

                adapter.notifyDataSetChanged();

                for(String username: users) {            //check in query if column 'isFollowing' contains any users and if they do, mark check to their name
                    if(ParseUser.getCurrentUser().getList("isFollowing").contains(username)) {
                        listView.setItemChecked(users.indexOf(username),true);
                    }
                }
            }
        });
    }
}