package com.example.mobilemediasecurity;

public class Call  {

    String name;
    String phone_no;
    String encrypted_phone;

    public Call(){}

    public Call(String name , String phone_no ,String encrypted_phone){
        this.name=name;
        this.phone_no=phone_no;
        this.encrypted_phone=encrypted_phone;
    }

    public void setName(String name)
    {

        this.name=name;
    }
    public void setPhone(String phone_no)
    {

        this.phone_no=phone_no;
    }
    public void setEncrypted_phone(String encrypted_phone)

    {
        this.encrypted_phone=encrypted_phone;
    }
    public String getName()
    {
        return name;
    }

    public String getPhone_no(){

        return phone_no;
    }
    public String getEncrypted_phone()
    {

        return encrypted_phone;
    }

}

