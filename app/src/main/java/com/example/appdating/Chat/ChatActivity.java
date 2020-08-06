package com.example.appdating.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
<<<<<<< HEAD
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appdating.Matches.MatchesActivity;
import com.example.appdating.Matches.MatchesAdapter;
import com.example.appdating.Matches.MatchesObject;
=======

>>>>>>> a2f2c5d02e5e3428c3b49aa39780e0d9f373a939
import com.example.appdating.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
<<<<<<< HEAD
    private RecyclerView.Adapter mChatAdapter, mMatchesAdapter;

    private RecyclerView.LayoutManager mChatLayoutManager, mMatchesLayoutManager;
    private String currentUserID, matchId, chatId;
    private EditText mSendEditText;
    private Button mSendButton;
    private Button mBack;
    private ImageView userImage;
    private TextView userName;

=======
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserID, matchId, chatId;
    private EditText mSendEditText;
    private Button mSendButton;
>>>>>>> a2f2c5d02e5e3428c3b49aa39780e0d9f373a939

    DatabaseReference mDatabaseUser, mDatabaseChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        matchId = getIntent().getExtras().getString("matchId");

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matchs").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatId();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
<<<<<<< HEAD
        mRecyclerView.setHasFixedSize(false);
=======
            mRecyclerView.setHasFixedSize(false);
>>>>>>> a2f2c5d02e5e3428c3b49aa39780e0d9f373a939

        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = (EditText) findViewById(R.id.edtMessage);
        mSendButton = (Button) findViewById(R.id.btnSend);
<<<<<<< HEAD

        userImage = (ImageView) findViewById(R.id.userImage);
        userName = (TextView) findViewById(R.id.userName);

        mMatchesLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), ChatActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);
        //éo có hope :)))
        getUserMatchId();

        mBack = (Button) findViewById(R.id.Back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
=======
>>>>>>> a2f2c5d02e5e3428c3b49aa39780e0d9f373a939
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

<<<<<<< HEAD
    private void getUserMatchId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matchs");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot match : snapshot.getChildren()) {
                        FecthMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FecthMatchInformation(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userId = snapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";
                    if (snapshot.child("name").getValue() != null) {
                        name = snapshot.child("name").getValue().toString();
                        userName.setText(name);
                    }
                    if (snapshot.child("profileImageUrl").getValue() != null) {
                        profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                        Glide.with(getApplication()).load(profileImageUrl).into(userImage);
                    }

                    //Đoạn này là để nó thêm cái name với image get được vào đó

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


=======
>>>>>>> a2f2c5d02e5e3428c3b49aa39780e0d9f373a939
    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if (!sendMessageText.isEmpty()) {
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createByUser", currentUserID);
            newMessage.put("text", sendMessageText);

            newMessageDb.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }

<<<<<<< HEAD

=======
>>>>>>> a2f2c5d02e5e3428c3b49aa39780e0d9f373a939
    private void getChatId() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chatId = snapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChatMessage() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
<<<<<<< HEAD
                    String message = null;
                    String createByUser = null;
=======
                    String message = "";
                    String createByUser = "";
>>>>>>> a2f2c5d02e5e3428c3b49aa39780e0d9f373a939
                    if (snapshot.child("text").getValue() != null) {
                        message = snapshot.child("text").getValue().toString();
                    }
                    if (snapshot.child("createByUser").getValue() != null) {
                        createByUser = snapshot.child("createByUser").getValue().toString();
                    }
                    if (message != null && createByUser != null) {
                        Boolean currentUserBoolean = false;
                        if (createByUser.equals(currentUserID)) {
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message,currentUserBoolean);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
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
<<<<<<< HEAD
    private ArrayList<MatchesObject> resultsMatches = new ArrayList<MatchesObject>();

    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }
=======
>>>>>>> a2f2c5d02e5e3428c3b49aa39780e0d9f373a939

    private ArrayList<ChatObject> resultsChat = new ArrayList<ChatObject>();

    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }
}