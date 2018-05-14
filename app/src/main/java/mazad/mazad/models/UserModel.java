package mazad.mazad.models;

import java.io.Serializable;

public class UserModel implements Serializable {

    String name;
    String email;
    String mobile;
    String role;
    String id;


    public UserModel(String name, String email, String mobile, String role, String id) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getRole() {
        return role;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setId(String id) {
        this.id = id;
    }
}
