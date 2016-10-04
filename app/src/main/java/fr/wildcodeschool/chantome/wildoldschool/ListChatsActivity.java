package fr.wildcodeschool.chantome.wildoldschool;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chantome on 27/09/2016.
 */
public class ListChatsActivity extends AppCompatActivity{

    private String chatName,chatDesc,temp_key,current_id, userkey,chatKey,meh;
    public final static String CHAT = "fr.wildcodeschool.chantome.wildoldschool.CHAT";
    private Button createBtn, btnDeco;
    private FirebaseAuth Auth;
    private Intent mainActivite;
    private View v_iew;
    private RadioButton radio_open, radio_close, radio_public;
    private TextView textChat;
    private boolean status = true;
    private boolean access;
    private static final String TAG = "WOS-ListChats";
    private User user;
    private Map<String ,User> users = new HashMap<String,User>();
    private ArrayList<Chat> mesChats = new ArrayList<Chat>();
    ArrayList<String> chatKeys= new ArrayList<String>();
    //pour l'adapter
    private Map<String,String> groupUsers = new HashMap<String,String>();;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference rootChats = FirebaseDatabase.getInstance().getReference().child("chats");
    private int count;
    private ListView list;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chats);
        Log.i(TAG,"CREATE");

        Auth = FirebaseAuth.getInstance();
        createBtn = (Button) findViewById(R.id.create_chat);
        btnDeco = (Button) findViewById(R.id.deco);
        list = (ListView) findViewById(R.id.list_chats);
        count=0;

        //Ajoute mes Utilisateur dans une Collection
        getUsers();
        getChatKeys();

        //Afficher ma liste de Chats
        FirebaseListAdapter<Chat> fireadapter = new FirebaseListAdapter<Chat>(
                ListChatsActivity.this,
                Chat.class,
                R.layout.list_chats,
                rootChats
        ) {
            @Override
            protected void populateView(View v, Chat model, int position) {
                Log.i(TAG, "ListChats-onChildAdded");

                Log.i(TAG, "Je déclare mes variables.");

                String monName = (String) model.getName().toString();
                String monAuthor = (String) model.getAuthor().toString();
                String maDesc = (String) model.getDesc().toString();
                boolean monStatus = Boolean.valueOf(model.isStatus());
                boolean monAccess = Boolean.valueOf(model.isAccess());

                Map<String, String> monGroupUser = new HashMap<String, String>();
                monGroupUser = model.getGroupUser();
                Log.i(TAG, "J'ai récupérer mes données qui sont mise en variables.");

                Chat monChat = new Chat(monName, monAuthor, maDesc, monStatus, monAccess, monGroupUser);

                Log.i(TAG, "J'ajoute mon Chat dans ma Collection de chats");
                mesChats.add(monChat);
                Log.i(TAG, "SUCCESS !");

                TextView txtName = (TextView) v.findViewById(R.id.chat_name);
                TextView txtDesc = (TextView) v.findViewById(R.id.chat_desc);

                txtName.setText(model.getName().toString());
                txtDesc.setText(model.getDesc().toString());
            }
        };

        list.setAdapter(fireadapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Envoi mon objet monstre selectionné vers l'activité details
                Intent secondeActivite = new Intent(ListChatsActivity.this, ChatActivity.class);
                Bundle mBundle = new Bundle();
                //mBundle.putSerializable(CHAT,mesChats.get(position));
                mBundle.putString("chatKey",chatKeys.get(position).toString());
                secondeActivite.putExtras(mBundle);
                startActivity(secondeActivite);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewChat();
            }
        });

        //Déconnexion de l'utilisateur
        btnDeco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.signOut();
                mainActivite = new Intent(ListChatsActivity.this, MainActivity.class);
                startActivity(mainActivite);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"START");

        //CA MARCHE !!!!!!!!!!!!!!!!!!
        /*
        FirebaseRecyclerAdapter<Chat, ChatViewHolder> mAdapter;

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recyclerView);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(
                Chat.class,
                R.layout.list_chats,
                ChatViewHolder.class,
                rootChats) {
            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, Chat model, int position) {
                viewHolder.nameText.setText(model.getName().toString());
                viewHolder.descText.setText(model.getDesc().toString());
            }
        };
        Log.i(TAG,"SET ADAPTER");
        recycler.setAdapter(mAdapter);
        */

    }
/*
    private static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView descText;

        public ChatViewHolder(View v) {
            super(v);
            nameText = (TextView) v.findViewById(R.id.chat_name);
            descText = (TextView) v.findViewById(R.id.chat_desc);
        }
    }
*/
    public void getListChat(DataSnapshot dataSnapshot) {

        Log.i(TAG, "ListChats-onChildAdded");
        Log.i(TAG, "Chat_id : " + dataSnapshot.getKey().toString());
        chatKey = dataSnapshot.getKey().toString();
        count++;
        Log.i(TAG,"COUNT : "+count+" | ");

        Log.i(TAG, "Je déclare mes variables.");

        String monName = (String) dataSnapshot.child("name").getValue().toString();
        String monAuthor = (String) dataSnapshot.child("author").getValue().toString();
        String maDesc = (String) dataSnapshot.child("desc").getValue().toString();
        boolean monStatus = Boolean.valueOf(dataSnapshot.child("status").getValue().toString());
        boolean monAccess = Boolean.valueOf(dataSnapshot.child("access").getValue().toString());

        Map<String, String> monGroupUser = new HashMap<String, String>();

        for (DataSnapshot datauser : dataSnapshot.child("groupusers").getChildren()) {
            monGroupUser.put(datauser.getKey(), datauser.getValue().toString());
        }

        Log.i(TAG, "J'ai récupérer mes données qui sont mise en variables.");

        Chat monChat = new Chat(monName, monAuthor, maDesc, monStatus, monAccess, monGroupUser);

        Log.i(TAG, "J'ajoute mon Chat dans ma Collection de chats");
        mesChats.add(monChat);
        //adapter.notifyDataSetChanged();
        Log.i(TAG, "SUCCESS !");
    }

    public void getChatKeys(){
        rootChats.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG,"onChildAdded : getChatKEY");
                chatKey = dataSnapshot.getKey().toString();
                Log.i(TAG,"onChildAdded : "+chatKey);
                chatKeys.add(chatKey);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                chatKey = dataSnapshot.getKey().toString();
                chatKeys.add(chatKey);
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

    public void getUsers(){

        root.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userkey = dataSnapshot.getKey().toString();
                Log.i(TAG,"USERKEY : "+userkey);
                if(dataSnapshot.hasChildren()) {
                    user = new User(dataSnapshot.child("firstname").getValue().toString());
                    users.put(userkey, user);
                }else{
                    Log.i(TAG,"dataSnapshot vide..");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                userkey = dataSnapshot.getKey().toString();
                Log.i(TAG,"USERKEY : "+userkey);
                if(dataSnapshot.hasChildren()) {
                    user = new User(dataSnapshot.child("firstname").getValue().toString());
                    users.put(userkey, user);
                }else{
                    Log.i(TAG,"dataSnapshot vide..");
                }
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

    public void addNewChat(){

        Log.i(TAG,"J'ai cliquer sur crée un nouveau chat.");

        AlertDialog.Builder builder = new AlertDialog.Builder(ListChatsActivity.this);

        builder.setTitle("Créer un chat");

        Log.i(TAG,"Jusqu'ici tout va bien !");
        LayoutInflater inflater = ListChatsActivity.this.getLayoutInflater();
        Log.i(TAG,"ATTENTION !");

        v_iew = inflater.inflate(R.layout.form_add_chat, null) ;
        builder.setView(v_iew);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                current_id = Auth.getCurrentUser().getUid();

                Log.i(TAG,"ID_USER : "+current_id);
                Log.i(TAG,"Création du Chat !!!");

                EditText editName, editDesc;
                editName = (EditText) v_iew.findViewById(R.id.chat_name);
                editDesc = (EditText) v_iew.findViewById(R.id.chat_desc);

                chatName = editName.getText().toString();
                chatDesc = editDesc.getText().toString();

                Log.i(TAG,"Nom du chat : "+chatName+" | "+chatDesc);
                if(status){
                    Log.i(TAG,"Chat \"Ouvert\".");
                }else{
                    Log.i(TAG,"Chat \"Fermé\".");
                }

                if(access){
                    Log.i(TAG,"Le chat est en privé.");
                }else{
                    Log.i(TAG,"Le chat est en publique.");
                }
                //Tableau temporaire des clés messages
                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = rootChats.push().getKey();
                root.updateChildren(map);


                //on ajout les variable dans le message qui a pour clé temp_key
                DatabaseReference rootChat = rootChats.child(temp_key);

                meh = users.get(current_id).getFirstname();
                Log.i(TAG,"NOM_USER : "+meh);

                groupUsers.put(current_id,meh);

                //monChat.setGroupUser(groupUsers);
                Chat monChat = new Chat(chatName,current_id,chatDesc,status,access,groupUsers);

                rootChat.setValue(monChat);
                Log.i(TAG,"OBJECT Chat envoyé en base");
                //rootChat.updateChildren(map2);
                //editMessage.setText("");
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Log.i(TAG,"OUF ! On va afficher la vue");
        builder.create();
        builder.show();
        Log.i(TAG,"Rock & Roll !!!");
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_status_open:
                if (checked)
                    status = true;
                    break;
            case R.id.radio_status_close:
                if (checked)
                    status = false;
                    break;
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_access:
                if (checked){
                    //Privé
                    access = true;
                }
                else
                {
                    //Publique
                    access = false;
                }
                break;
        }
    }
}