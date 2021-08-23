package com.example.media_security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
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

public class VideoGallery extends AppCompatActivity {

        List<Video> arrayList;
        VideoAdapter adapter=null;
        RecyclerView recyclerView;
        FirebaseAuth auth;
        String UserID;
        Video  video;
        Intent intent;
        String video_uri;
        FirebaseFirestore fstore;
        String folder;
        SimpleExoPlayer simpleExoPlayer;
        DatabaseReference databaseReference;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_video_gallert);

            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            arrayList = new ArrayList<>();
            intent=getIntent();

            adapter = new VideoAdapter(VideoGallery.this, arrayList);
            recyclerView.setAdapter(adapter);
            view_all_videos();
        }


        public void  view_all_videos() {
            auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            UserID = auth.getCurrentUser().getUid();
            fstore.collection("users").document(UserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.getResult().exists()) {
                        Toast.makeText(VideoGallery.this, "loading decrypted videos....", Toast.LENGTH_SHORT).show();

                        folder = task.getResult().getString("email").replace("@gmail.com", "_firebase");
                        databaseReference = FirebaseDatabase.getInstance().getReference(folder).child("Videos");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    video = dataSnapshot.getValue(Video.class);
                                    video.setUid(dataSnapshot.getKey());
                                    arrayList.add(video);
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
