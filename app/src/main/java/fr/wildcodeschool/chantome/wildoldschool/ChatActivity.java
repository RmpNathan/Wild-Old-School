package fr.wildcodeschool.chantome.wildoldschool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chantome on 03/10/2016.
 */
public class ChatActivity extends AppCompatActivity{

    private DatabaseReference rootChat,rootChatMessages,rootChatMessage;
    private String TAG = "WOS-Chat";
    private RecyclerView chatMessages;
    private EditText editMessage;
    private Button send;
    private String monChat, monMessage, monUser,temp_key;
    private FirebaseAuth Auth;
    private Message message;
    private RecyclerView recycler;
    private Map<String,String> groupUsers= new HashMap<String, String>();
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mAdapter;
    private TextView introText;
    private String chatName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.i(TAG,"What's Up ?! ");

        Auth = FirebaseAuth.getInstance();
        monChat = (String) getIntent().getStringExtra("chatKey");
        rootChat = FirebaseDatabase.getInstance().getReference().child("chats").child(monChat);
        rootChatMessages = rootChat.child("messages");
        chatMessages = (RecyclerView) findViewById(R.id.conversation);
        editMessage = (EditText) findViewById(R.id.send_message);
        send = (Button) findViewById(R.id.send);
        introText = (TextView) findViewById(R.id.intro);

        getGroupUsers();

        rootChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatName = dataSnapshot.child("name").getValue().toString();
                Log.i(TAG,"Le nom du Chat est "+chatName);
                introText.setText("Bienvenu dans le chat : "+chatName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG,databaseError.getMessage().toString());
            }
        });

        recycler = (RecyclerView) findViewById(R.id.conversation);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        mAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.list_messages,
                MessageViewHolder.class,
                rootChatMessages
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.nameText.setText(groupUsers.get(model.getUid().toString()));
                viewHolder.messageText.setText(model.getMessage().toString());
            }
        };

        recycler.setAdapter(mAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Envoi message..");
                monUser = Auth.getCurrentUser().getUid().toString();
                monMessage = editMessage.getText().toString();

                //Tableau temporaire des clés messages
                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = rootChatMessages.push().getKey().toString();
                rootChatMessages.updateChildren(map);

                rootChatMessage = rootChatMessages.child(temp_key);
                message = new Message(monUser,monMessage);
                rootChatMessage.setValue(message);
                Log.i(TAG,"c'est bon..");


                //Efface le text du champs message aprés l'envoi de celui-ci
                editMessage.setText("");
                Log.i(TAG,"on nettoit..");
            }
        });

    }

    private static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView messageText;

        public MessageViewHolder(View v) {
            super(v);
            nameText = (TextView) v.findViewById(R.id.user);
            messageText = (TextView) v.findViewById(R.id.message);
        }
    }

    private void getGroupUsers(){
        Log.i(TAG,"Récupérer les utilisateurs dans le chat..");
        rootChat.child("groupUser").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                groupUsers.put(dataSnapshot.getKey().toString(),dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                groupUsers.put(dataSnapshot.getKey().toString(),dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG,databaseError.getMessage());
            }
        });
    }

}
