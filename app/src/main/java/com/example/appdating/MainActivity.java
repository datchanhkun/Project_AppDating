package com.example.appdating;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.appdating.Cards.arrayAdapter;
import com.example.appdating.Cards.cards;
import com.example.appdating.Matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //gọi bên class card qua
    private cards cards_data[];
    //cũng gọi bên kia qua
    private com.example.appdating.Cards.arrayAdapter arrayAdapter;
    private int i;

    private FirebaseAuth mAuth;

    private String currentUId;
    private DatabaseReference userDb;


    ListView listView;
    List<cards> rowItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        checkUserSex();

        rowItems = new ArrayList<cards>();
        // của thằng bên kia
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);


        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            // Khi kéo card qua bên phải
            @Override
            public void onLeftCardExit(Object dataObject) {
                //lấy ID của người dùng
                cards obj = (cards)dataObject;
                String userId = obj.getUserId();
                userDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            // Khi kéo card qua bên trái
            @Override
            public void onRightCardExit(Object dataObject) {
                //lấy ID của người dùng
                cards obj = (cards)dataObject;
                String userId = obj.getUserId();
                userDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                //có match chưa
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isConnectionMatch(String userId) {
        //confirming a match
        DatabaseReference currentUserConnectionsDb = userDb.child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    //khi 2 người match nhau
                    Toast.makeText(MainActivity.this, "Kết nối mới", Toast.LENGTH_SHORT).show();
                    //saving matches to database
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    userDb.child(snapshot.getKey()).child("connections").child("matchs").child(currentUId).setValue(true);
                    userDb.child(snapshot.getKey()).child("connections").child("matchs").child(currentUId).child("ChatId").setValue(key);

                    userDb.child(currentUId).child("connections").child("matchs").child(snapshot.getKey()).setValue(true);
                    userDb.child(currentUId).child("connections").child("matchs").child(snapshot.getKey()).child("ChatId").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String userSex;
    private String oppositeUserSex;

    public void checkUserSex() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDB = userDb.child(user.getUid());
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("sex").getValue() != null) {
                        userSex = dataSnapshot.child("sex").getValue().toString();
                        oppositeUserSex = "Female";
                        switch (userSex) {
                            case "Male":
                                oppositeUserSex = "Female";
                                break;
                            case "Female":
                                oppositeUserSex = "Male";
                                break;
                        }
                        getOppositeSexUsers();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getOppositeSexUsers() {
        userDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    //nếu vuốt sang trái hoặc sang phải thì không hiện người đó lên nữa
                if(snapshot.exists() &&  !snapshot.child("connections").child("nope").hasChild(currentUId)
                        &&  !snapshot.child("connections").child("yeps").hasChild(currentUId)
                        && snapshot.child("sex").getValue().toString().equals(oppositeUserSex)) {

                    String profileImageUrl = "default";
                    if(!snapshot.child("profileImageUrl").getValue().equals("default")) {
                        profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                    }
                    cards item = new cards(snapshot.getKey(), snapshot.child("name").getValue().toString(), profileImageUrl);
                    rowItems.add(item);
                    //báo cho nó biết là mình update lại dữ liệu
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }

    public void goToSettings(View view) {

        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}