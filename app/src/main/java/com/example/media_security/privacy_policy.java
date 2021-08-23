package com.example.media_security;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link privacy_policy#newInstance} factory method to
 * create an instance of this fragment.
 */
public class privacy_policy extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public privacy_policy() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment privacy_polict.
     */
    // TODO: Rename and change types and number of parameters
    public static privacy_policy newInstance(String param1, String param2) {
        privacy_policy fragment = new privacy_policy();
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
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_privacy_polict, container, false);
        TextView textView=(TextView)v.findViewById(R.id.textView);
        TextView textView1=(TextView)v.findViewById(R.id.heading);
        textView1.setText("\n\t\t\t\t\t\t\t\t\tPrivacy and Policy");
        textView.append("\n •This application is developed in android studio.\n\n");
        textView.append("•\tThis android app concentrates on securing data on mobile phones by storing it in an encrypted form.\n\n");
        textView.append("•\tThis android application has divided the phones content into contacts , images, files, videos, and messages to simplify the user.\n\n");
        textView.append("•\t The system will access your mobile contacts, images , videos and files.\n\n");
        textView.append("•\tThis is an advanced system that helps the user to hide his private or confidential data in an encrypted way and away from his phone.\n\n");
        textView.append("•\tAll mobile data will be in encrypted form only the owner of the mobile phone can access the data.\n\n");
        textView.append("•\tThe system encrypts the data and saves on the database which helps the user in two ways.\n\n");
        textView.append("\t\t\t1)The data is encrypted using the users key and it can be decrypted only by him.\n\n");
        textView.append("\t\t\t2)If the mobile is lost , he can have his data just by logging into app in some other phone.\n\n");
        textView.append("•\tIf user forget the private key so the data encrypted with that key cannot be decrypted.\n\n");
        textView.append("                      \n");

        return v;

    }
}
