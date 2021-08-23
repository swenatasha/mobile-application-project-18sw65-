package com.example.media_security;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.tasks.Continuation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class ContactsOption extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore fstore;
    String folder  ;
    String phone_no , name1;
    public static String key ;
    public static String encryptedText;
    TextView number1;
    UploadTask upload_contact;
    public static String algorithm = "AES";

    DatabaseReference firebaseDatabase;
    StorageReference firebaseStorage;

    public static final int pick_contact = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_option);
        auth = FirebaseAuth.getInstance();
        number1 = (TextView) findViewById(R.id.number1);
        fstore = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser()!= null) {
            Task<DocumentSnapshot> documentSnapshotTask = fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        folder = task.getResult().getString("email").replace("@gmail.com", "_firebase");
                        firebaseDatabase = FirebaseDatabase.getInstance().getReference(folder).child("Contacts");
                        firebaseStorage = FirebaseStorage.getInstance().getReference();
                        viewData();

                    }
                }
            });
        } else {
            Toast.makeText(ContactsOption.this, "Login OR create account ", Toast.LENGTH_SHORT).show();
        }

        number1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!number1.getText().toString().isEmpty()) {
                    decryption();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.add:
                adding();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void adding() {
        if (auth.getCurrentUser() != null) {
            fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        key=task.getResult().getString("private key");
                        Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI); // It is used the provide the directory of the phone.
                       in.putExtra(Intent.EXTRA_ALLOW_MULTIPLE , true);
                        startActivityForResult(in, pick_contact);
                        // startActivityForResult(): it  allows to start activity and get some data back. Imagine that you have some file picker activity.
                        // You can start it and when user chooses the file, the result is given back to the original activity.
                    }
                }
            });
        } else {
            Toast.makeText(ContactsOption.this, " Email authentication require ", Toast.LENGTH_LONG).show();

        }

    }
    @Override
    //we need to override the onActivityResult method that is invoked automatically when second activity returns result.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The resultCode will be RESULT_CANCELED if the activity explicitly returned that, didn't return any result,
        if (resultCode == RESULT_OK) {
            //requestCode is result of activity
            switch (requestCode) {
                case pick_contact:
                    contactPicked(data);
                    break;
            }
        } else {
            Toast.makeText(this, "failed to load contact", Toast.LENGTH_SHORT).show();
        }
    }


    private void contactPicked(Intent data) {
        // cursor: ​ Cursor is an object, which is returned by a query. It point to a single row of the result fetched by the query.
        Cursor cursor = null;
        try {

            // Uri(Uniform Resource Identifier): It is used to identify a resource.
            Uri uri = data.getData();
            // getContentResolver(): It is query to android phone.
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();  // It allows to perform a test whether the query returned an empty set or not
            int phone_index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phone_no = cursor.getString(phone_index);
            name1 = cursor.getString(name);
            encrypt(phone_no , key);
            uploadContact();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void encrypt(String text, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IllegalBlockSizeException, InvalidKeyException {
        SecretKeySpec keySpec = generateKey(password);   //It can be used to construct a SecretKey from a byte array
        Cipher c = Cipher.getInstance(algorithm);  // This class provides the functionality of a cryptographic cipher for encryption and decryption
        c.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedValue = c.doFinal(text.getBytes());
        encryptedText = Base64.encodeToString(encryptedValue, Base64.DEFAULT);


    }

    public static SecretKeySpec generateKey(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //Message digests are secure one-way hash functions that take arbitrary-sized data and output a fixed-length hash value.
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");  // unicode transformation formar
        digest.update(bytes, 0, bytes.length);    // The data is processed through it using the update methods.
        byte[] key = digest.digest();  // Once all the data to be updated has been updated, one of the digest methods should be called to complete the hash computation.
        SecretKeySpec secret = new SecretKeySpec(key, "AES");
        return secret;
    }


    public void uploadContact()
    {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading......\t\t");
        dialog.show();
        Toast.makeText(ContactsOption.this, " Wait until contact is uploading....", Toast.LENGTH_SHORT).show();

        fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                {
                    folder=task.getResult().getString("email");
                    StorageReference reference = firebaseStorage.child(folder + System.currentTimeMillis());
                    upload_contact=reference.putBytes(phone_no.getBytes());
                    Task<Uri> urlTask = upload_contact.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                com.example.mobilemediasecurity.Call call = new com.example.mobilemediasecurity.Call(name1 , phone_no , encryptedText);
                                firebaseDatabase.child(firebaseDatabase.push().getKey()).setValue(call);
                                Toast.makeText(ContactsOption.this, " Contacts uploaded  ", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }
                        }
                    });

                }
                else{
                    Toast.makeText(ContactsOption.this ," Login OR create your account " , Toast.LENGTH_LONG).show();
                }
            }
        });


    }



    public void viewData() {
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    if (data != null) {
                        if (!(number1.getText().toString().contains(data.get("name").toString()))) {
                            number1.append("Name : " + data.get("name").toString() + "\n");
                        }
                        if (!(number1.getText().toString().contains(data.get("encrypted_phone").toString().substring(4, 20)))) {
                            number1.append(".Encrypted Phone:   " + data.get("encrypted_phone").toString().substring(4, 20) + "\n\n");
                        }
                    } else{
                        Toast.makeText(getApplicationContext() , "no contacts uploaded " , Toast.LENGTH_LONG).show();
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ContactsOption.this, " Upload contacts", Toast.LENGTH_SHORT).show();
            }
        });
    }




    public void viewData2() {
        Toast.makeText(ContactsOption.this, "loading decrypted contacts....", Toast.LENGTH_SHORT).show();

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            number1.append("Name : " + data.get("name").toString() + "\n");
                            number1.append(".Phone_no:   " + data.get("phone_no").toString() + "\n\n");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ContactsOption.this, "no contacts uploaded yet ", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void decryption()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactsOption.this);
        View view = getLayoutInflater().inflate(R.layout.private_key_alert, null);

        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button ok = (Button) view.findViewById(R.id.ok);
        EditText editText = (EditText) view.findViewById(R.id.private_key);
        ImageView eye=(ImageView)view.findViewById(R.id.eye);

        builder.setView(view);

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        Toast.makeText(ContactsOption.this ,"Enter the private key to decrypt the data "  , Toast.LENGTH_LONG).show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
//
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                if (auth.getCurrentUser() != null){
                    fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                if (editText.getText().toString().equals(task.getResult().getString("private key"))) {
                                    number1.setText("");
                                    viewData2();
                                } else {
                                    Toast.makeText(ContactsOption.this, " Incorrect key !! ", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    });
                }
                else {
                    Toast.makeText(ContactsOption.this, "Login OR create account ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}