package com.example.media_security;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Authentication#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Authentication extends Fragment {
    Button login;
    EditText email, password;
    Button forgetPass;
    Button create_acc;
    ImageView eye;
    FirebaseAuth auth;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Authentication() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Authentication.
     */
    // TODO: Rename and change types and number of parameters
    public static Authentication newInstance(String param1, String param2) {
        Authentication fragment = new Authentication();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_authentication, container, false);
        login = (Button) v.findViewById(R.id.register);
        email = (EditText) v.findViewById(R.id.email);
        password = (EditText) v.findViewById(R.id.password);
        eye = (ImageView) v.findViewById(R.id.eye);
        create_acc = (Button) v.findViewById(R.id.create_acc);
        forgetPass = (Button) v.findViewById(R.id.forgotPass);


        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                forgetPass(email.getText().toString()
                );
            }
        });


        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });


        create_acc.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Sign_up.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(email.getText().toString(), password.getText().toString());
            }
        });
        return v;

    }


    public void loginUser(final String email, final String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "wrong credentials", Toast.LENGTH_LONG).show();
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "login in successfully ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Login failed\ncreate account or Check your internet connection ", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    public void forgetPass(String email) {

            if (auth.getCurrentUser().getUid() != null) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), " Check your email to reset the password ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Error , check your internet connection ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } else {
                Toast.makeText(getActivity(), " create your  Account", Toast.LENGTH_LONG).show();
            }


    }
}

