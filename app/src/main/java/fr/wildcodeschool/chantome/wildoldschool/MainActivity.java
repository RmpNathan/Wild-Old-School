package fr.wildcodeschool.chantome.wildoldschool;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    // Firebase instance variables
    public FirebaseAuth mFirebaseAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public Intent authActivite;
    public Intent chatsActivite;
    private static final String TAG = "WOS-Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG,"Hello World !!");

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Vérifie si un utilisateur est déjà connecté
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.i(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    chatsActivite = new Intent(MainActivity.this, ListChatsActivity.class);
                    startActivity(chatsActivite);
                } else {
                    // User is signed out
                    Log.i(TAG, "onAuthStateChanged:signed_out");
                    authActivite = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(authActivite);
                }
            }
        };

        mAuthListener.onAuthStateChanged(mFirebaseAuth);

    }
}
