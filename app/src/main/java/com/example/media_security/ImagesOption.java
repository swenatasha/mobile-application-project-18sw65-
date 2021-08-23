package com.example.media_security;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class ImagesOption extends AppCompatActivity {


    String key;
    String encryptedText;
    String uri;
    static String folder;
    TextView number1;
    UploadTask upload_image;
    DatabaseReference firebaseDatabase;
    StorageReference firebaseStorage;
    FirebaseFirestore fstore;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_option);

        number1 = (TextView) findViewById(R.id.number1);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        if(auth.getCurrentUser()!=null) {
            fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.getResult().exists()) {
                        folder = task.getResult().getString("email").replace("@gmail.com", "_firebase");
                        firebaseDatabase = FirebaseDatabase.getInstance().getReference(folder).child("Images");
                        firebaseStorage = FirebaseStorage.getInstance().getReference();
                        key=task.getResult().getString("private key");
                        viewData();
                    }
                }
            });
        }
        else {
            Toast.makeText(ImagesOption.this, "Login OR create account ", Toast.LENGTH_SHORT).show();
        }

        number1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!number1.getText().toString().isEmpty()) {
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

    public  void decryption(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ImagesOption.this);
        View view = getLayoutInflater().inflate(R.layout.private_key_alert, null);

        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button ok = (Button) view.findViewById(R.id.ok);
        ImageView eye=(ImageView)view.findViewById(R.id.eye);
        EditText editText = (EditText) view.findViewById(R.id.private_key);

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
        Toast.makeText(ImagesOption.this, "Enter the private key to decrypt the data ", Toast.LENGTH_LONG).show();
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
                                    Intent intent = new Intent(ImagesOption.this, ImagesGallery.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ImagesOption.this, " Incorrect key !! ", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    });
                } else {
                    Toast.makeText(ImagesOption.this, "Login OR create account ", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void adding() {

        if(ContextCompat.checkSelfPermission(ImagesOption.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (auth.getCurrentUser() != null) {
                fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            selectImage();
                        }
                    }
                });
            } else {
                Toast.makeText(ImagesOption.this, "Login OR create account", Toast.LENGTH_LONG).show();
            }
        }
        else{
            ActivityCompat.requestPermissions(ImagesOption.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }


    public void selectImage() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "select image"), 100);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            Toast.makeText(ImagesOption.this, "failed to load the images", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    //we need to override the onActivityResult method that is invoked automatically when second activity returns result.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            uri = data.getData().toString();
            try {
                encrypt(uri);
                uploadImage();
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
//            }
            }
        }
    }


    private void encrypt(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeyException {
        SecretKeySpec keySpec = generateKey(key);   //It can be used to construct a SecretKey from a byte array
        Cipher c = Cipher.getInstance("AES");  // This class provides the functionality of a cryptographic cipher for encryption and decryption
        c.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedValue = c.doFinal(text.getBytes());
        encryptedText = Base64.encodeToString(encryptedValue, Base64.DEFAULT);


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


    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadImage() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading......\t\t");
        dialog.show();
        Toast.makeText(ImagesOption.this, " Wait until image is uploading....", Toast.LENGTH_SHORT).show();

        if(auth.getCurrentUser()!=null) {
            fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.getResult().exists()) {
                        folder = task.getResult().getString("email").replace("@gmail.com", "_firebase");
                        StorageReference reference = firebaseStorage.child(folder + System.currentTimeMillis() + "." + getFileExtension(Uri.parse(uri)));
                        upload_image = reference.putFile(Uri.parse(uri));

                        Task<Uri> urlTask = upload_image.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                   Image uploadPdf = new Image(encryptedText, downloadUri.toString());
                                    firebaseDatabase.child(firebaseDatabase.push().getKey()).setValue(uploadPdf);
                                    Toast.makeText(ImagesOption.this, "Image  uploaded \n Refresh the images", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                }
                            }
                        });
                    }
                }
            });
        }
        else {
            Toast.makeText(ImagesOption.this, " Email Authentication is required ", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }


    public void viewData() {
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    if(data!=null){
                    if (!(number1.getText().toString().contains(data.get("encryptedUri").toString().substring(86, 109)))) {
                        number1.append("Image: " + data.get("encryptedUri").toString().substring(86, 109) + "\n\n");
                    }

                }else{
                    Toast.makeText(getApplicationContext() , " upload images " , Toast.LENGTH_LONG).show();
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

}
