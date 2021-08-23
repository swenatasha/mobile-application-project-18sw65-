package com.example.media_security;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ImagesGallery extends AppCompatActivity {

    List<Image> arrayList;
    ImageAdapter adapter=null;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    String UserID;
    ChildEventListener childEventListener;
    Image image;
    FirebaseFirestore fstore;
    String folder;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_gallery);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        arrayList = new ArrayList<>();


        adapter = new ImageAdapter(ImagesGallery.this, arrayList);
        recyclerView.setAdapter(adapter);

        View_all_images();


    }


    public void  View_all_images() {
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        UserID = auth.getCurrentUser().getUid();
        fstore.collection("users").document(UserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()) {
                    Toast.makeText(getApplicationContext() , "Loading decrypted images" , Toast.LENGTH_LONG).show();
                    folder = task.getResult().getString("email").replace("@gmail.com", "_firebase");
                    databaseReference = FirebaseDatabase.getInstance().getReference(folder).child("Images");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                image = dataSnapshot.getValue(Image.class);
                                image.setUid(dataSnapshot.getKey());
                                arrayList.add(image);
                                adapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
    }
