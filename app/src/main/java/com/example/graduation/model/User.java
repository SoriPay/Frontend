package com.example.graduation.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class User {
        //implements Parcelable { // 회원가입 요청시 보낼 데이터

    @SerializedName("id")
    private String id; // 이게 id
    @SerializedName("password")
    private String password;

    @SerializedName("name")
    private String name;

    public User(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public User() {

    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected User(Parcel in) {
        id = in.readString();
        password = in.readString();
        name = in.readString();
    }

   /* public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(password);
        parcel.writeString(name);
    }



    */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }




}