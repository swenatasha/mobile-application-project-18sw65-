package com.example.media_security;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

public class FilesOption extends AppCompatActivity {

    EditText textView;
    UploadTask upload_file;
    TextView number1;
    String key;
    String encrypted_doc;
    String UserID;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    String folder;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_option);

        number1 = (TextView) findViewById(R.id.number1);


        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.getResult().exists()) {
                        folder = task.getResult().getString("email").replace("@gmail.com", "_firebase");
                        databaseReference = FirebaseDatabase.getInstance().getReference(folder).child("files");
                        storageReference = FirebaseStorage.getInstance().getReference();
                        key=task.getResult().getString("private key");
                        viewData();
                    }
                }
            });
        } else {
            Toast.makeText(FilesOption.this, "Login OR create account ", Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(FilesOption.this);
                        View view = getLayoutInflater().inflate(R.layout.upload_file, null);
                        textView = (EditText) view.findViewById(R.id.name);
                        Button upload = (Button) view.findViewById(R.id.upload);
                        Button cancel = (Button) view.findViewById(R.id.cancel);
                        builder.setView(view);

                        AlertDialog alert = builder.create();
                        alert.setCanceledOnTouchOutside(false);
                        alert.show();


                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alert.dismiss();
                            }
                        });
                        upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (textView.getText().toString() == null) {
                                    Toast.makeText(FilesOption.this, "Enter the Document name", Toast.LENGTH_LONG).show();
                                }
                                selectDocument();
                                alert.dismiss();
                            }
                        });

                    }
                }
            });

        } else {
            Toast.makeText(FilesOption.this, "Email authentication require ", Toast.LENGTH_LONG).show();
        }
    }


    public void selectDocument() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        startActivityForResult(Intent.createChooser(intent, "select document"), 12);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                encrypt(data.getData().toString());
                uploadPDF(data.getData());
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }

        }
    }


    public void decryption() {


        AlertDialog.Builder builder = new AlertDialog.Builder(FilesOption.this);
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
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                if(auth.getCurrentUser()!=null) {
                    fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                if (editText.getText().toString().equals(task.getResult().getString("private key"))) {
                                    Intent intent = new Intent(FilesOption.this, FilesList.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(FilesOption.this, " Incorrect key !! ", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    });
                }
                else {
                    Toast.makeText(FilesOption.this, "Email authentication require ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void uploadPDF(Uri uri) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading......\t\t");
        dialog.show();
        Toast.makeText(FilesOption.this, " Wait until file is uploading....", Toast.LENGTH_SHORT).show();

        UserID = auth.getCurrentUser().getUid();
        fstore.collection("users").document(UserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    folder = task.getResult().getString("email").replace("@gmail.com" , "_firebase");
                    StorageReference reference = storageReference.child(folder + System.currentTimeMillis() + ".pdf");
                    upload_file = reference.putFile(uri);
                    Task<Uri> urlTask = upload_file.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                Uri downloadUri = task.getResult();
                                UploadFile uploadPdf = new UploadFile(textView.getText().toString(), downloadUri.toString(), encrypted_doc);
                                databaseReference.child(databaseReference.push().getKey()).setValue(uploadPdf);
                                Toast.makeText(FilesOption.this, " Document uploaded ", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }
                            else{
                                Toast.makeText(FilesOption.this, "oops!! no internet connection..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(FilesOption.this , "Email Authentication required " , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void encrypt(String imageUri) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException, InvalidKeyException {
        SecretKeySpec keySpec = generateKey(key);   //It can be used to construct a SecretKey from a byte array
        Cipher c = Cipher.getInstance("AES");  // This class provides the functionality of a cryptographic cipher for encryption and decryption
        c.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedValue = c.doFinal(imageUri.getBytes());
        encrypted_doc = Base64.encodeToString(encryptedValue, Base64.DEFAULT);

    }

    public static SecretKeySpec generateKey(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //Message digests are secure one-way hash functions that take arbitrary-sized data and output a fixed-length hash value.
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);    // The data is processed through it using the update methods.
        byte[] key = digest.digest();  // Once all the data to be updated has been updated, one of the digest methods should be called to complete the hash computation.
        SecretKeySpec secret = new SecretKeySpec(key, "AES");
        return secret;
    }



    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void viewData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        if(!(number1.getText().toString().contains(data.get("document_name").toString())))
                            number1.append(".Document Name : " + data.get("document_name").toString()+"\n");

                        if(!(number1.getText().toString().contains(data.get("encrypted").toString().substring(24 , 45))))
                            number1.append(".Encrypted doc:   " + data.get("encrypted").toString().substring(24 , 45)+"\n\n");

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FilesOption.this , " Upload images" , Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void decryptedText(String cipherText, String key) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec keySpec = generateKey(key);   //It can be used to construct a SecretKey from a byte array
        Cipher c = Cipher.getInstance("AES");  // This class provides the functionality of a cryptographic cipher for encryption and decryption
        c.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedText = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] b = c.doFinal(decryptedText);
        String  text = new String(b);
    }



}







