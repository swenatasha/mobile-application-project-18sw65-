package com.example.media_security;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilesList extends AppCompatActivity {


    DatabaseReference databaseReference;
    ListView list;
    String url;
    String folder;
    String UserID;
    String[] uploads;

    int pdf_size;
    int name_size;
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    FirebaseStorage firebaseStorage;
    List<UploadFile> list_pdf_view_screen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_list);

        list = (ListView) findViewById(R.id.list_view);
        list_pdf_view_screen = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();

        view_all_files();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UploadFile pdf=list_pdf_view_screen.get(position);
                WebView webview = new WebView(FilesList.this);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setAllowFileAccessFromFileURLs(true);
                webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
                webview.getSettings().setPluginState(WebSettings.PluginState.ON);
                webview.getSettings().setUseWideViewPort(true);
                webview.getSettings().setDomStorageEnabled(true);
                webview.getSettings().setBuiltInZoomControls(true);
                try {
                    url = URLEncoder.encode(pdf.getUri(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (webview.getContentHeight() == 0) {
                    webview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
                        }
                    }, 1000);
                    setContentView(webview);
                }
            }
        });

    }

    public void view_all_files() {
        UserID = auth.getCurrentUser().getUid();
        fstore.collection("users").document(UserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    folder = task.getResult().getString("email").replace("@gmail.com" , "_firebase");
                    databaseReference = FirebaseDatabase.getInstance().getReference(folder).child("files");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                if(!(data.get("uri")==null)){
                                    UploadFile pdf=new UploadFile();
                                    pdf.setUri(data.get("uri").toString());
                                    list_pdf_view_screen.add(pdf);
                                }

                                uploads = new String[list_pdf_view_screen.size()];
                                for (int i = 0; i<uploads.length ; i++) {
                                    if (!(list_pdf_view_screen.get(i).getUri() == null)) {
                                        uploads[i] = list_pdf_view_screen.get(i).getUri().substring(0  ,50);
                                    }
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, uploads) {

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    return view;
                                }
                            };
                            list.setAdapter(adapter);
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
