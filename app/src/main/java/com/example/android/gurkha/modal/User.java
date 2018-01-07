package com.example.android.gurkha.modal;

/**
 * Created by Shaakya on 3/24/2017.
 */

public class User {
    private int id;
    private String name;
    private String email;
    public String password;
    private String district;
    private String contact_no;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContactNo() {
        return contact_no;
    }

    public void setContactNo(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getDistrictName() {
        return district;
    }

    public void setDistrictName(String district) {
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

