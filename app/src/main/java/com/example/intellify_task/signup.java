package com.example.intellify_task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity
{

    private EditText userEmail, userPassword, userAge, userCity, userName;
    private EditText passPass;
    private Button createAccountButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView googleSignup;
    private  int RC_SIGN_IN=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userEmail=findViewById(R.id.signup_email);
        userPassword=findViewById(R.id.signup_password);
        userAge=findViewById(R.id.signup_age);
        userCity=findViewById(R.id.signup_city);
        userName=findViewById(R.id.signup_name);
        createAccountButton=findViewById(R.id.signup_button);
        passPass=findViewById(R.id.confirm_password);
        loadingBar=new ProgressDialog(this);
        googleSignup=findViewById(R.id.google_signup);

        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        createAccountButton.setOnClickListener(v -> CreateNewAccount());
//
//            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//
//        googleSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signIn();
//            }
//        });


    }

    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void CreateNewAccount()
    {

        final String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();
        String confirmPassword=passPass.getText().toString();
        final String city=userCity.getText().toString();
        final String age=userAge.getText().toString();
        final String name=userName.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please Write Your Email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please Write Your Password",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this,"Please confirm your  password",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(city))
        {
            Toast.makeText(this,"Please enter your address",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(age))
        {
            Toast.makeText(this,"Please enter your age",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"Please enter your Name",Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating Account...");
            loadingBar.setMessage("Please wait while creating your Account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {

                            FirebaseUser currentFirebaseUser = mAuth.getInstance().getCurrentUser() ;
                            System.out.println(currentFirebaseUser.getUid());

                            Toast.makeText(signup.this,"You are Authenticated Successfully...",Toast.LENGTH_SHORT).show();

                            Map<String, Object> user2 = new HashMap<>();
                            user2.put("name", name);
                            user2.put("city", city);
                            user2.put("age", age);
                            user2.put("email",email);

                            db.collection("users").document(currentFirebaseUser.getUid())
                                    .set(user2);
                            loadingBar.dismiss();
                            SendUserToHomeActivity();
                        }
                        else
                        {
                            String message=task.getException().getMessage();
                            Toast.makeText(signup.this,"Error Occured"+message,Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    });

        }

    }

    private void SendUserToHomeActivity()
    {
        Intent mainActivityIntent=new Intent(signup.this,Home.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();

    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN)
//        {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }



//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//
//            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//            if (acct != null) {
//                String personName = acct.getDisplayName();
//                String personGivenName = acct.getGivenName();
//                String personFamilyName = acct.getFamilyName();
//                String personEmail = acct.getEmail();
//                String personId = acct.getId();
//                Uri personPhoto = acct.getPhotoUrl();
//            }
//            SendUserToHomeActivity();
//
//
//        } catch (ApiException e) {
//
//            Log.v("error : ", "signInResult:failed code=" + e.getStatusCode());
//            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
//        }
//    }

}