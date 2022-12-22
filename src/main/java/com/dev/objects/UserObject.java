package com.dev.objects;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class UserObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    public int id;

    @Column
    private String userName;

    @Column
    private String token;

    @Override
    public String toString() {
        return "UserObject{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
