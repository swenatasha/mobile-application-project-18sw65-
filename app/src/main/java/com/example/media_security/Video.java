package com.example.media_security;

import androidx.annotation.Nullable;

public class Video {


    public String videoUri;
    private String uid;

    public String encryptedUri;

    public Video()
    {}

    public void setUid(String uid){
        this.uid=uid;
    }



    public Video(String encryptedUri , String videoUri  , String uid)
    {
        this.encryptedUri=encryptedUri;
        this.videoUri=videoUri;
        this.uid=uid;

    }

    public Video(String encryptedUri , String videoUri )
    {
        this.encryptedUri=encryptedUri;
        this.videoUri=videoUri;

    }

    public String getVideoUri()
    {

        return videoUri;
    }

    public String getUid()
    {

        return uid;
    }
    public String getEncryptedUri()

    {
        return encryptedUri;
    }


}
