package com.example.intellify_task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    private TextView registerText;
    private EditText userEmail,userPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmail =(EditText) findViewById(R.id.login_email);
        userPassword=(EditText) findViewById(R.id.login_password);
        loginButton= findViewById(R.id.login_button);
        loadingBar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        registerText=findViewById(R.id.register_text);


        loginButton.setOnClickListener(v -> AllowingUserToLogin());

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToSignupActivity();
            }
        });
    }

    private void sendUserToSignupActivity()
    {
        Intent mainActivityIntent=new Intent(MainActivity.this,signup.class);
        startActivity(mainActivityIntent);
    }

    private void AllowingUserToLogin()
    {
        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(MainActivity.this,"Enter Your Email",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(MainActivity.this,"Enter Your Password",Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingBar.setTitle("Logging in...");
            loadingBar.setMessage("Please wait while Logging in");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                SendUserToHomeActivity();
                                Toast.makeText(MainActivity.this,"You are logged in successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                            else
                            {
                                String message=task.getException().getMessage();
                                Toast.makeText(MainActivity.this,"Error Occured"+message,Toast.LENGTH_SHORT).show();
                                System.out.println(email +" "+password);
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }

    private void SendUserToHomeActivity()
    {
        Intent mainActivityIntent=new Intent(MainActivity.this,Home.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser!=null)
        {
            SendUserToHomeActivity();
        }
    }
}