package org.poream.dejaview.Java;

/**
 * Created by 이승호 on 2017-10-11.
 */

public class UserProfile {

    public String image;
    public String userFullName;
    public String email;
    public String firstName;
    public String lastName;
    public String location;
    public int age;
    public boolean gender;

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserProfile(String image,  String firstName, String lastName,String email, String location, int age, boolean gender) {
        this.image = image;
        this.userFullName = firstName+" "+lastName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.age = age;
        this.gender = gender;
    }
}
