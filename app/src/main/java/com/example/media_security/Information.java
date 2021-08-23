package com.example.media_security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Information extends AppCompatActivity {

    FirebaseAuth auth;
    String mail , name , pass ,key;
    FirebaseFirestore fstore;
    TextView username , email , password ,p_key;
    Button change_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        auth=FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        username=(TextView)findViewById(R.id.Name);
        p_key=(TextView)findViewById(R.id.key);
        change_password=(Button)findViewById(R.id.change_password);
        email=(TextView)findViewById(R.id.email);
        password=(TextView)findViewById(R.id.password);
        if(auth.getCurrentUser()!=null){
            fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.getResult().exists()){
                         mail=task.getResult().getString("email");
                         name=task.getResult().getString("name");
                         pass=task.getResult().getString("Password");
                         key=task.getResult().getString("private key");

                        email.setText("Email\t" + mail);
                        username.setText("username\t\t" + name);
                        password.setText("password\t" + pass);
                        p_key.setText("private key\t" + key);

                    }
                }
            });


        }


        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(Information.this);
                View view=getLayoutInflater().inflate(R.layout.change_password , null);

                alertDialog.setView(view);

                AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(false);
                alert.setTitle("Change Password");
                alert.show();
                EditText old=(EditText) view.findViewById(R.id.old);
                EditText new_p=(EditText)view. findViewById(R.id.new_p);
                Button ok1=(Button)view.findViewById(R.id.ok);

                ok1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(old.getText().toString().equals(pass)){
                            fstore.collection("users").document(auth.getCurrentUser().getUid()).update("Password" , new_p.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isComplete()){
                                        Toast.makeText(getApplicationContext() , "password changed successfully" , Toast.LENGTH_LONG).show();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext() , "unable to change " , Toast.LENGTH_LONG).show();

                                }
                            });
                    }
                        else{
                            Toast.makeText(getApplicationContext() , "old password is incorrect" , Toast.LENGTH_LONG).show();

                        }
                }
            });
            }
        });

    }
}
