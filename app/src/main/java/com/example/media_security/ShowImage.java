 package com.example.media_security;

 import android.content.Intent;
 import android.os.Bundle;
 import android.view.GestureDetector;
 import android.view.MotionEvent;
 import android.view.ScaleGestureDetector;
 import android.widget.ImageView;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.fragment.app.Fragment;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.firestore.FirebaseFirestore;
 import com.squareup.picasso.Picasso;

public class ShowImage extends AppCompatActivity  implements ScaleGestureDetector.OnScaleGestureListener  , GestureDetector.OnGestureListener {

    ImageView imageView;
    Intent intent;
    Image image;
    FirebaseFirestore fstore;
    private float  scale= 1f;
    private float onScaleBegin =0;
    private float onScaleEnd =0;

    FirebaseAuth auth;
    Fragment frag;
    GestureDetector detector;
    ScaleGestureDetector scaleGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        frag=null;
        imageView=(ImageView)findViewById(R.id.image);
        intent=getIntent();
        detector=new GestureDetector(this , this);
        scaleGestureDetector =new ScaleGestureDetector(this ,this);
        detector.setIsLongpressEnabled(true);

        image=new Image();
        auth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        Picasso.with(this)
                .load(intent.getStringExtra("Image_Uri"))
                .fit()
                .centerCrop()
                .into(imageView);

    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        scale*=detector.getScaleFactor();
        scale=Math.max(0.1f , Math.min(scale , 5.0f));
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        onScaleBegin=scale;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        onScaleEnd=scale;
        if(onScaleEnd > onScaleBegin){
            imageView.setScaleX(onScaleEnd/onScaleBegin);
        }

        if(onScaleEnd < onScaleBegin){
            imageView.setScaleY(onScaleBegin/onScaleEnd);
        }

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.delete_menu , menu );
//        return true;
//
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.delete:
//            fstore.collection("users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                     if(task.getResult().exists()){
//                         folder = task.getResult().getString("email").replace("@gmail.com", "_firebase");
//                         DatabaseReference firebaseDatabase=FirebaseDatabase.getInstance().getReference(folder);
//                         Log.i("firebase" , firebaseDatabase.toString());
//                           StorageReference   storageReference =FirebaseStorage.getInstance().getReferenceFromUrl(intent.getStringExtra("Image_Uri"));
//                         storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                             @Override
//                             public void onSuccess(Void aVoid) {
//                                 firebaseDatabase.child("Images").child(user).setValue(null);
//                                 Toast.makeText(getApplicationContext() , "Image deleted " , Toast.LENGTH_LONG).show();
//
//                             }
//                         }).addOnFailureListener(new OnFailureListener() {
//                             @Override
//                             public void onFailure(@NonNull Exception e) {
//                                 Toast.makeText(getApplicationContext()   , " no del " , Toast.LENGTH_LONG).show();
//
//                             }
//                         });
//                         firebaseDatabase.child("Images").child("-Mfn252oadCrVcbdwMla").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                             @Override
//                             public void onSuccess(Void aVoid) {
//                                 Toast.makeText(getApplicationContext()   , "del " , Toast.LENGTH_LONG).show();
//
//                             }
////                         }).addOnFailureListener(new OnFailureListener() {
//                             @Override
//                             public void onFailure(@NonNull Exception e) {
//                                 Toast.makeText(getApplicationContext()   , " no del " , Toast.LENGTH_LONG).show();
//
//                             }
//                         });

//                         firebaseDatabase.removeValue(new DatabaseReference.CompletionListener() {
//                                                          @Override
//                                                          public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                                                               if(ref==null)
//                                                               {
//                                                                   Toast.makeText(getApplicationContext() ,"deleted " , Toast.LENGTH_LONG).show();
//
//                                                               }
//                                                               else{
//                                                               StorageReference    storageReference =firebaseStorage.getReferenceFromUrl(intent.getStringExtra("Image_Uri"));
//                                 storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                     @Override
//                                     public void onSuccess(Void aVoid) {
//                                         firebaseDatabase.removeValue();
//                                         firebaseDatabase.setValue("");
//                                         Toast.makeText(getApplicationContext() , "Image deleted " , Toast.LENGTH_LONG).show();
//
//                                     }
//                                 });
//                                                                       }
//                                                          }
//                                                      });
//                         firebaseDatabase.child(currentUsers).addValueEventListener(new ValueEventListener() {
//                             @Override
//                             public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                 storageReference =firebaseStorage.getReferenceFromUrl(intent.getStringExtra("Image_Uri"));
//                                 storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                     @Override
//                                     public void onSuccess(Void aVoid) {
//                                         Toast.makeText(getApplicationContext() , "delted " , Toast.LENGTH_LONG).show();
//                                         firebaseDatabase.child(currentUsers).removeValue();
//                                     }
//                                 });
//                             }
//
//                             @Override
//                             public void onCancelled(@NonNull DatabaseError error) {
//
//                             }
//                         });
//                         firebaseDatabase.child("Images").equalTo(intent.getStringExtra("Image_Uri")).addListenerForSingleValueEvent(new ValueEventListener() {
//                             @Override
//                             public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                 for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                                     String key = dataSnapshot.getKey();
//                                     Log.i("key of show image "  , key);
//
//                                 }
//                             }
//
//                             @Override
//                             public void onCancelled(@NonNull DatabaseError error) {
//
//                             }
//                         });

//                         firebaseDatabase.child("Images").push().getKey().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                             @Override
//                             public void onComplete(@NonNull Task<Void> task) {
//                                  if(task.isComplete()){
//                                      Toast.makeText(getApplicationContext() , "deleted" , Toast.LENGTH_LONG).show();
//                                  }
//                             }
//                         });

//                         storageReference =firebaseStorage.getReferenceFromUrl(intent.getStringExtra("Image_Uri"));
//                         storageReference1=firebaseStorage.getReference(intent.getStringExtra("Image_encrypted"));
//                         storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                             @Override
//                             public void onSuccess(Void aVoid) {
//                                 firebaseDatabase.child(firebaseDatabase.push().getKey()).removeValue();
//                                 Toast.makeText(getApplicationContext() , "Image deleted " , Toast.LENGTH_LONG).show();
//
//                             }
//                         });
//                         storageReference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                             @Override
//                             public void onSuccess(Void aVoid) {
//                                 firebaseDatabase.child(firebaseDatabase.push().getKey()).removeValue();
//                                 Toast.makeText(getApplicationContext() , "Image deleted " , Toast.LENGTH_LONG).show();
//
////                             }
////                         });
//                     }
//                }
//            });
//        }
//        return  super.onOptionsItemSelected(item);
//    }


}
