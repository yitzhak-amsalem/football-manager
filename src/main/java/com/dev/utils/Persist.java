
package com.dev.utils;

import com.dev.objects.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void createConnectionToDatabase () {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/football_project?allowPublicKeyRetrieval=true&useSSL=false", "root", "1234");
            System.out.println("Successfully connected to DB");
            setGroups();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setGroups(){
        Session session = sessionFactory.openSession();
        List<GroupObject> groups = getAllGroups();
        System.out.println(groups.size()); // todo check and delete
        if (groups.size() == 0){
            String[] groupsNames = {"Man City","Man United", "Chelsea"
                    , "Barcelona","Real Madrid", "Seville"
                    , "PSG", "Marseille", "Juventus"
                    ,"Inter", "Bayern Munich","Dortmund"};
            for (String groupName: groupsNames){
                GroupObject group = new GroupObject();
                group.setGroupName(groupName);
                session.save(group);
            }
        }

    }
    public void setGame(){
        Session session = sessionFactory.openSession();
        Game game1 = new Game();
        game1.setGroupA(getGroupByGroupName("Barcelona"));
        game1.setGroupB(getGroupByGroupName("Real Madrid"));
        game1.setGoalsGroupA(5);
        game1.setGoalsGroupB(0);
        game1.setLive(false);
        session.save(game1);
        Game game2 = new Game();
        game2.setGroupA(getGroupByGroupName("Man City"));
        game2.setGroupB(getGroupByGroupName("Man United"));
        game2.setGoalsGroupA(2);
        game2.setGoalsGroupB(1);
        game2.setLive(false);
        session.save(game2);
        Game game3 = new Game();
        game3.setGroupA(getGroupByGroupName("Chelsea"));
        game3.setGroupB(getGroupByGroupName("Inter"));
        game3.setGoalsGroupA(1);
        game3.setGoalsGroupB(1);
        game3.setLive(false);
        session.save(game3);
        Game game4 = new Game();
        game4.setGroupA(getGroupByGroupName("Juventus"));
        game4.setGroupB(getGroupByGroupName("Marseille"));
        game4.setGoalsGroupA(4);
        game4.setGoalsGroupB(3);
        game4.setLive(false);
        session.save(game4);
        Game game5 = new Game();
        game5.setGroupA(getGroupByGroupName("Real Madrid"));
        game5.setGroupB(getGroupByGroupName("Barcelona"));
        game5.setGoalsGroupA(2);
        game5.setGoalsGroupB(6);
        game5.setLive(false);
        session.save(game5);
        session.close();
    }
    public GroupObject getGroupByGroupName(String groupName){
        Session session = sessionFactory.openSession();
        List<GroupObject> availableGroups = session.createQuery("FROM GroupObject WHERE groupName = :groupName")
                .setParameter("groupName", groupName).list();
        session.close();
        if (availableGroups.size() == 1){
            return availableGroups.get(0);
        }
        return null;
    }

    public List<GroupObject> getAvailableGroups () {
        Session session = sessionFactory.openSession();
        List<GroupObject> availableGroups = session.createQuery("FROM GroupObject WHERE inLive = " + false).list();
        session.close();
        return availableGroups;
    }
    public List<GroupObject> getAllGroups () {
        Session session = sessionFactory.openSession();
        List<GroupObject> groups = session.createQuery("FROM GroupObject ").list();
        session.close();
        return groups;
    }
    public void setGroupInLive(String groupName){
        Session session = sessionFactory.openSession();
        GroupObject groupToUpdate = session.get(GroupObject.class, groupName);
        System.out.println("before update" + groupToUpdate);
        groupToUpdate.setInLive(true);
        GroupObject groupToUpdate1 = session.get(GroupObject.class, groupName);
        System.out.println("after update" + groupToUpdate1);
        session.close();
    }

    public void getGroupDetails (TeamRank teamRank) {
        Session session = sessionFactory.openSession(); //
        List<Game> games = session.createQuery("FROM Game WHERE groupA.groupName = :groupNameA or groupB.groupName = :groupNameB" )
                .setParameter("groupNameA", teamRank.getGroupName())
                .setParameter("groupNameB", teamRank.getGroupName())
                .list();
        for (Game game: games){
            teamRank.updateGroupDetails(game);
        }
        session.close();
    }
/*    public void getGroupLiveDetails (GroupObject group) {
        Session session = sessionFactory.openSession();
        List<Game> games = session.createQuery("FROM Game WHERE groupA = " + group.getGroupName() + " OR groupB = " + group.getGroupName()).list(); //TODO update group details from results
        for (Game game: games){
            group.updateGroupDetails(game);
        }
        System.out.println("update: " + group);
        session.close();
    }*/
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        try {
            ResultSet resultSet =
                    this.connection
                            .createStatement()
                            .executeQuery("SELECT username, token FROM users");
            while (resultSet.next()) {
                String token = resultSet.getString("token");
                String username = resultSet.getString("username");
                User user = new User(username, token);
                allUsers.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allUsers;
    }


    public void addUser (String username, String token) {
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
    }

    public boolean usernameAvailable (String username) {
        boolean available = false;
        try {
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
        }
        return available;
    }

    public User getUserByToken (String token) {
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
    }

    public void addNote (int userId, String content) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO notes (content, user_id) VALUE (?, ?)");
            preparedStatement.setString(1, content);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
        }
    }

    public List<String> getNotesByUserId (int userId) {
        List<String> notes = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT content FROM notes WHERE user_id = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String content = resultSet.getString("content");
                notes.add(content);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return notes;
    }

    public String getUserByCreds (String username, String token) {
        String response = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND token = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                response = token;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

}
