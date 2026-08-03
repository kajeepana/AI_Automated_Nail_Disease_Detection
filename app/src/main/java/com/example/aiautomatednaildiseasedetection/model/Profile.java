package com.example.aiautomatednaildiseasedetection.model;

public class Profile {

    private String firstName;
    private String lastName;
    private String email;
    private String dob;
    private int age;

    public Profile() {
    }

    public Profile(String firstName, String lastName, String email, String dob, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dob = dob;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}