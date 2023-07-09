package com.gnj.e_koperasi;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class User {
    private String name;
    private String customer_id;
    private String email;
    private String phone;
    private String password;

    public User() {
        // Empty constructor required for Firebase.
    }

    public User(String name, String customer_id, String email, String phone, String password) {
        this.name = name;
        this.customer_id = customer_id;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() { return password; }
}
