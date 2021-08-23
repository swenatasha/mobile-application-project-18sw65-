package com.example.media_security;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment frag = null;
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    BiometricManager manager;
    BiometricPrompt.PromptInfo promptInfo;
    BiometricPrompt prompt;
    Executor executor;
    boolean home=false ;
    boolean info=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(method);
        frag = new privacy_policy();

        getSupportFragmentManager().beginTransaction().replace(R.id.relative, frag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.info:
                if(auth.getCurrentUser()!=null) {
                    authentication();
                    info=true;
                    home=false;

                }
                else{
                    Toast.makeText(getApplicationContext() , "No user exists\tSign in to proceed further" , Toast.LENGTH_LONG).show();
                    }
                break;
            case R.id.logout:
                Intent intent =new Intent(getApplicationContext() , Logout.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener method = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.privacy:
                    frag = new privacy_policy();
                    getSupportFragmentManager().beginTransaction().replace(R.id.relative, frag).commit();
                    break;
                case R.id.authentication:
                    frag = new Authentication();
                    getSupportFragmentManager().beginTransaction().replace(R.id.relative, frag).commit();
                    break;
                case R.id.home:
                    authentication();
                    home=true;
                    info=false;
                    break;

            }

            return true;
        }
    };

    public void authentication() {

        manager = BiometricManager.from(MainActivity.this);
        switch (manager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(MainActivity.this, " Biometric hardware currently unavailable", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(MainActivity.this, "No suitable hardware for biometric", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(MainActivity.this, "No biometric enrolled", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
        }


        executor = ContextCompat.getMainExecutor(MainActivity.this);

        prompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if(info){
                    Intent intent = new Intent(MainActivity.this, Information.class);
                    startActivity(intent);
                }
                if(home){
                    frag=new Home();
                    getSupportFragmentManager().beginTransaction().replace(R.id.relative, frag).commit();
                }
            }
            @Override
            public void onAuthenticationError ( int errcode, @NonNull CharSequence string){
                super.onAuthenticationError(errcode, string);
            }

            @Override
            public void onAuthenticationFailed () {
                super.onAuthenticationFailed();
                frag=new privacy_policy();
                getSupportFragmentManager().beginTransaction().replace(R.id.relative , frag).commit();
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

