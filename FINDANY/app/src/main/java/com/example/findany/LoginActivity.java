package com.example.findany;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 1;
    public static String PREF_NAME="My_User_Name";

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount gsa;
    ImageView signinbutton;

    private FirebaseAuth mAuth;
    final LoadingDialog loadingDialog=new LoadingDialog(LoginActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        signinbutton=findViewById(R.id.signin);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

    }

    private void SignIn() {

        Intent signinintnet=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signinintnet,RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loadingDialog.startingloding();
                Toast.makeText(this, "Google Sign in Succeeded",  Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences=getSharedPreferences(LoginActivity.PREF_NAME,0);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putBoolean("HAS_LOGED_IN",true);
                editor.commit();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google Sign in Failed " + e,  Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idtoken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idtoken , null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email=user.getEmail();
                            String requiredDomain = "klu.ac.in";
                            if (email.endsWith(requiredDomain)) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);                           }

                            Log.d(TAG, "signInWithCredential:success:currentUser : " + user.getEmail());
                            loadingDialog.CancleLoading();
                            Toast.makeText(LoginActivity.this, "Firebase Authentication Succeeded ",  Toast.LENGTH_LONG).show();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Firebase Authentication failed:" + task.getException(),  Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}