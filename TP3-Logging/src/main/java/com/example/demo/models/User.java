package com.example.demo.models;

import jakarta.persistence.*;


@Entity(name="users")
@Access(AccessType.FIELD)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private long userId;
    
    @Column(name="name")
    private String name;

    @Column(name="email", unique = true)
    private String email;
    
    @Column(name="password")
    private String password;
    
    @Column(name="age")
    private int age;



    public int getAge() {
    	return this.age;
    }
    
    public void setAge(int age) {
    	this.age=age;
    }
    public String getName() {
    	return this.name;
    }
    public void setName(String name) {
    	this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}