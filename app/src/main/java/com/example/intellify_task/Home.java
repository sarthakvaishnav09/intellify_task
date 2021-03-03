package com.example.intellify_task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.bind.ArrayTypeAdapter;

public class Home extends AppCompatActivity
{

    private ArrayAdapter adapter;
    private Button logout;
    private DocumentReference docref;
    private FirebaseUser currentFirebaseUser;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView ansText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String[] userInfo = {"Name","Email ID","City","Age"};

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = mAuth.getInstance().getCurrentUser() ;

        adapter=new ArrayAdapter<String>(this, R.layout.list_tile,userInfo);
        logout=findViewById(R.id.home_logout);
        ListView listView=findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        ansText=findViewById(R.id.home_ans);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = (String) parent.getItemAtPosition(position);

                DocumentReference docRef = db.collection("users").document(currentFirebaseUser.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if("Name".equals(selectedItem))
                                    ansText.setText(document.get("name").toString());

                                else if(selectedItem.equals("Email ID"))
                                    ansText.setText(document.get("email").toString());

                                else if(selectedItem.equals("City"))
                                    ansText.setText(document.get("city").toString());

                                else
                                    ansText.setText(document.get("age").toString());


                            } else {
                                System.out.println("No such document");
                                //Log.d(TAG, "No such document");
                            }
                        }
                        else
                        {
                            System.out.println(task.getException());
                            //Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mAuth.signOut();
                sendUserToLoginActivity();
            }
        });

    }

    private void sendUserToLoginActivity()
    {
        Intent loginIntent=new Intent(Home.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}