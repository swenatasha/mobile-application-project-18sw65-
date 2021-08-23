package com.example.media_security;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class Sign_up extends AppCompatActivity {

    EditText email;
    EditText password;
    Button register;
    EditText name;
    EditText private_key;
    FirebaseAuth auth;
    String mail;
    String name1;
    String pass;
    String key;
    String UserID;
    FirebaseFirestore fstore;
    Button finger_print;
    BiometricManager manager;
    BiometricPrompt.PromptInfo promptInfo;
    BiometricPrompt prompt;
    Executor executor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        auth = FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        email = (EditText) findViewById(R.id.email);
        name=(EditText)findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        private_key=(EditText)findViewById(R.id.private_key);
        register = (Button) findViewById(R.id.register);
        finger_print=(Button)findViewById(R.id.biometric);




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mail = email.getText().toString();
                pass = password.getText().toString();
                key=private_key.getText().toString();
                name1 = name.getText().toString();

                if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass) ||TextUtils.isEmpty(key) || TextUtils.isEmpty(name1)) {
                    Toast.makeText(Sign_up.this, " Wrong credentials ", Toast.LENGTH_SHORT).show();
                }
                if (pass.length()<6 ) {
                    Toast.makeText(Sign_up.this, "password is too short", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerUser(mail, pass, name1, key);
                }
            }
        });

        finger_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               authentication();
            }
        });

    }

    public void registerUser (final String email, String password  , final String name, final String key ){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Sign_up.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    UserID = auth.getCurrentUser().getUid();
                    fstore.collection("users").document(UserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                Toast.makeText(Sign_up.this, "Already the user\nLogin", Toast.LENGTH_LONG).show();
                            } else {
                                storeData(email, name, key);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(Sign_up.this, "Check your internet connection  ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void storeData(String email , String name ,String key){
        Map<String, Object> data=new HashMap<>();
        data.put("email" , auth.getCurrentUser().getEmail());
        data.put("name" , name);
        data.put("private key" , key);
        data.put("Password" , pass);
        fstore.collection("users").document(UserID).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Sign_up.this  , "Processing your request , be patient " , Toast.LENGTH_LONG).show();
                if(task.isSuccessful()){
                    Toast.makeText(Sign_up.this , "Registered Successfully \n  Sign in to proceed further ..." , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void authentication()
    {
        manager = BiometricManager.from(Sign_up.this);
        switch (manager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(Sign_up.this, " Biometric hardware currently unavailable", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(Sign_up.this, "No suitable hardware for biometric", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(Sign_up.this , "No biometric enrolled", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
        }
        executor = ContextCompat.getMainExecutor(Sign_up.this);
        prompt = new BiometricPrompt(Sign_up.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(Sign_up.this, "successfully enroll the biometric ", Toast.LENGTH_SHORT).show();
            }

            public void onAuthenticationError(int errcode, @NonNull CharSequence string) {
                super.onAuthenticationError(errcode, string);
            }
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Sign_up.this , "Fingerprint not recognized ....\nTry again" ,Toast.LENGTH_LONG).show();
            }

        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Login using finger Print")
                .setNegativeButtonText("Cancel")
                .build();

        prompt.authenticate(promptInfo);
    }
}
