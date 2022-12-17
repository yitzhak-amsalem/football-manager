
package com.dev.utils;

import com.dev.controllers.TestController;
import com.dev.objects.NoteObject;
import com.dev.objects.User;
import com.dev.objects.UserObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class Persist {

    private Connection connection;

    private final SessionFactory sessionFactory;

    @Autowired
    public Persist (SessionFactory sf) {
        this.sessionFactory = sf;
    }
    @Autowired
    private TestController testController;

    @PostConstruct
    public  void creatFirstUser(){

        UserObject userObject = new UserObject();
        String username="manager";
        String password="12345678";
        userObject.setUsername(username);
        String token = testController.createHash(username, password);
        userObject.setToken(token);
        if(!usernameExist(username)) {
            saveUser(userObject);
        }

    }
/*    public void createConnectionToDatabase () {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/football_project", "root", "1234");
            System.out.println("Successfully connected to DB");
            System.out.println();
            UserObject userObject = new UserObject();
            userObject.setToken("dvsd");
            userObject.setUsername("ya");
            saveUser(userObject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

  /*  public List<UserObject> getAllUsersHibernate () {
        Session session = sessionFactory.openSession();
        UserObject userObject = new UserObject();
        session.save(userObject);
        List<UserObject> userObjects = session.createQuery("FROM UserObject ").list();
        session.close();
        return userObjects;
    }*/

    public void saveUser(UserObject userObject){
        sessionFactory.openSession().save(userObject);
    }




/*    public void addUser (String username, String token) {
        try {
            PreparedStatement preparedStatement =
                    this.connection
                            .prepareStatement("INSERT INTO users (username, token) VALUE (?,?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, token);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    public boolean usernameExist (String username) {
        boolean exist = false;
        String response = null;
        Session session = sessionFactory.openSession();
        List<UserObject> users =session.createQuery("select token FROM UserObject where username= :username").setParameter("username",username).list();
        session.close();
        if(users.size()==1){
            exist=true;
        }
     /*   try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT id " +
                    "FROM users " +
                    "WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                available = false;
            } else {
                available = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        return exist;
    }

/*    public User getUserByToken (String token) {
        User user = null;
        try {
            PreparedStatement preparedStatement = this.connection
                    .prepareStatement(
                            "SELECT id, username FROM users WHERE token = ?");
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                user = new User(username, token);
                user.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }*/



    public String getUserByCreds (String username, String token) {
        String response = null;
        Session session = sessionFactory.openSession();
        Query query=session.createQuery(" FROM UserObject where username= :username AND token= :token");
        query.setParameter("username",username);
        query.setParameter("token",token);
        List<UserObject> users=query.getResultList();
        if(users.size()==1) {
            response = users.get(0).getToken();
        }
        System.out.println(response);
        session.close();
       /* try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND token = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                response = token;
            }
        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }*/
        return response;
    }

}
