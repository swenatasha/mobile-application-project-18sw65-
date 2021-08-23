package com.example.media_security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;
public class Logout extends AppCompatActivity {


    FirebaseAuth auth;
    FirebaseFirestore fstore;
    EditText email;
    EditText password;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        auth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        email=(EditText) findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        logout=(Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(getApplicationContext() , "wrong credentials " , Toast.LENGTH_LONG).show();
                }
                else{
                    if(auth.getCurrentUser()!=null) {
                       fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               if(task.getResult().exists()){
                                   String mail=task.getResult().getString("email");
                                   Log.i("mail" , mail);
                                   String pass=task.getResult().getString("Password");
                                   if(email.getText().toString().equals(mail)){
                                       if( password.getText().toString().equals(pass)) {
                                           auth.signOut();
                                           Toast.makeText(getApplicationContext(), "successfully log out", Toast.LENGTH_LONG).show();
                                           Fragment frag = new privacy_policy();
                                           getSupportFragmentManager().beginTransaction().add(R.id.privacy , frag ).addToBackStack(null).commit();
//                                           Intent intent=new Intent(Logout.this  , privacy_policy.class);
//                                           startActivity(intent);
                                       }
                                   }
                                   else{
                                       Toast.makeText(getApplicationContext() , "No  "+email.getText().toString()+" exists" , Toast.LENGTH_LONG).show();

                                   }
                               }
                           }
                       });
                    }
                    else{
                        Toast.makeText(getApplicationContext() , "No user exists " , Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

}
