package fr.wildcodeschool.chantome.wildoldschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chantome on 21/09/2016.
 */
public class AuthActivity extends AppCompatActivity {

    public String email;
    public String password;
    public String pseudo;
    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "WOS-Auth";
    public EditText editEmail;
    public EditText editPassword;
    public EditText editPseudo;
    public Button button;

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        editEmail = (EditText) findViewById(R.id.email_address);
        editPassword = (EditText) findViewById(R.id.password);
        editPseudo = (EditText) findViewById(R.id.pseudo);
        button = (Button) findViewById(R.id.connect);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.i(TAG,"J'ai cliquer sur le bouton connexion");

            email = editEmail.getText().toString();
            password = editPassword.getText().toString();

            pseudo = editPseudo.getText().toString();

            if(email.isEmpty() || password.isEmpty() || pseudo.isEmpty()){
                Log.i(TAG,"champs vides");
            }
            else{
                Log.i(TAG,"les champs sont rempli");

                mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                //Erreur d'authentification
                                Log.i(TAG,"Erreur d'authentification");
                            }else{
                                Log.i(TAG,"YOLOOOOOOOOO");
                                //On ajout l'utilisateur dans notre Firebase

                                //verifier si l'utilisateur existe en base de donnée
                                String Uid = mAuth.getCurrentUser().getUid();
                                User monUser = new User(pseudo);

                                myRef.child("users").child(Uid).setValue(monUser);

                                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                }
            });
    }
}
