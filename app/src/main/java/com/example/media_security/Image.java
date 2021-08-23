package com.example.media_security;

import androidx.annotation.Nullable;

public class Image {

    public String imageUri;
    private String uid;

    public String encryptedUri;

    public Image()
    {}

    public void setUid(String uid){
        this.uid=uid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Image){
            Image image=(Image)obj;
            return  this.uid.equals(image.getUid());

        }
        else
        {
            return false;
        }
    }

    public Image(String encryptedUri , String imageUri  , String uid)
    {
        this.encryptedUri=encryptedUri;
        this.imageUri=imageUri;
        this.uid=uid;

    }

    public Image(String encryptedUri , String imageUri )
    {
        this.encryptedUri=encryptedUri;
        this.imageUri=imageUri;

    }

    public String getImageUri()
    {

        return imageUri;
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
