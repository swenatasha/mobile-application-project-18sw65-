package com.example.media_security;

public class UploadFile {


    String document_name;
    String uri;
    String encrypted;
    String uid;
    public UploadFile(){}

    public UploadFile(String document_name , String uri , String encrypted , String uid)
    {
        this.document_name=document_name;
        this.uri=uri;
        this.encrypted=encrypted;
        this.uid=uid;

    }
    public UploadFile(String document_name , String uri , String encrypted )
    {
        this.document_name=document_name;
        this.uri=uri;
        this.encrypted=encrypted;
    }



    public void setDocument_name(String document_name)
    {
        this.document_name=document_name;
    }

    public void setUri(String uri)
    {
        this.uri=uri;
    }

    public String getUri()
    {
        return uri;
    }

    public String getUid()
    {
        return uid;
    }
    public void setUid()
    {
        this.uid=uid;
    }

    public void setEncrypted(String encrypted)
    {
        this.encrypted=encrypted;
    }

    public String getEncrypted()
    {
        return encrypted;
    }
    public String getDocument_name()
    {
        return document_name;
    }

}