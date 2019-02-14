package com.example.digitaldashboard;

//TODO Create a data structure for getting the user information
public class UserDataStructure {
    private String first_Name;
    private String last_Name;
    private String email;
    private String country;

    public UserDataStructure() {

    }

    public UserDataStructure(String First_Name, String Last_Name, String Email, String Country) {
        this.first_Name = First_Name;
        this.last_Name = Last_Name;
        this.email = Email;
        this.country = Country;
    }

    public String getFname() {
        return first_Name;
    }

    public void setFname(String First_Name) {
        this.first_Name = First_Name;
    }

    public String getLname() {
        return last_Name;
    }

    public void setLname(String last_Name) {
        this.last_Name = last_Name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String Email) {
        this.email = Email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String Country) {
        this.country = Country;
    }
}