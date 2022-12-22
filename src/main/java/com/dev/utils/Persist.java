
package com.dev.utils;

import com.dev.objects.*;

import com.dev.objects.UserObject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Component
public class Persist {
    private final SessionFactory sessionFactory;
    @Autowired
    public Persist (SessionFactory sf) {
        this.sessionFactory = sf;
    }
    @Autowired
    private Utils utils;

    @PostConstruct
    public void initBasicDetails(){
        setGroups();
        setFirstUser();
        //setGames();
    }
    public void setFirstUser(){
        addUser("manager","12345678");
    }
    public void setGroups(){
        Session session = sessionFactory.openSession();
        List<GroupObject> groups = getAllGroups();
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
    public void setGames(){
        Session session = sessionFactory.openSession();
        System.out.println("manager: " + getUserByDetails("manager","12345678"));
        Game game1 = new Game();
        game1.setGroupA(getGroupByGroupName("Barcelona"));
        game1.setGroupB(getGroupByGroupName("Real Madrid"));
        game1.setGoalsGroupA(5);
        game1.setGoalsGroupB(0);
        game1.setUserManager(getUserByDetails("manager","12345678"));
        game1.setLive(false);
        session.save(game1);
        Game game2 = new Game();
        game2.setGroupA(getGroupByGroupName("Man City"));
        game2.setGroupB(getGroupByGroupName("Man United"));
        game2.setGoalsGroupA(2);
        game2.setGoalsGroupB(1);
        game2.setUserManager(getUserByDetails("manager","12345678"));
        game2.setLive(false);
        session.save(game2);
        Game game3 = new Game();
        game3.setGroupA(getGroupByGroupName("Chelsea"));
        game3.setGroupB(getGroupByGroupName("Inter"));
        game3.setGoalsGroupA(1);
        game3.setGoalsGroupB(1);
        game3.setUserManager(getUserByDetails("manager","12345678"));
        game3.setLive(false);
        session.save(game3);
        Game game4 = new Game();
        game4.setGroupA(getGroupByGroupName("Juventus"));
        game4.setGroupB(getGroupByGroupName("Marseille"));
        game4.setGoalsGroupA(4);
        game4.setGoalsGroupB(3);
        game4.setUserManager(getUserByDetails("manager","12345678"));
        game4.setLive(true);
        session.save(game4);
        Game game5 = new Game();
        game5.setGroupA(getGroupByGroupName("Real Madrid"));
        game5.setGroupB(getGroupByGroupName("Barcelona"));
        game5.setGoalsGroupA(2);
        game5.setGoalsGroupB(6);
        game5.setUserManager(getUserByDetails("manager","12345678"));
        game5.setLive(true);
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
        List<GroupObject> availableGroups = getAllGroups();
        List<Game> gamesInLive = session.createQuery("FROM Game WHERE isLive = true").list();
        List<GroupObject> groupsInLive = new ArrayList<>();
        for (Game game: gamesInLive){
            groupsInLive.add(game.getGroupA());
            groupsInLive.add(game.getGroupB());
        }
        availableGroups.removeAll(groupsInLive);
        session.close();
        return availableGroups;
    }
    public List<GroupObject> getAllGroups () {
        Session session = sessionFactory.openSession();
        List<GroupObject> groups = session.createQuery("FROM GroupObject ").list();
        session.close();
        return groups;
    }
    public List<Game> getLiveGames () {
        Session session = sessionFactory.openSession();
        List<Game> liveGames = session.createQuery("FROM Game WHERE isLive = true ")
                .list();
        session.close();
        return liveGames;
    }
    public List<Game> getLiveGamesPerUser (String token) {
        Session session = sessionFactory.openSession();
        List<Game> liveGamesPerUser = null;
        liveGamesPerUser = session.createQuery("FROM Game WHERE isLive = true AND userManager = :user")
                .setParameter("user", getUserByToken(token))
                .list();
        for (Game game: liveGamesPerUser){
            System.out.println(game.getGroupA().getGroupName());
        }
        session.close();
        return liveGamesPerUser;
    }
    public void getGroupDetails (TeamRankLive teamRank, boolean withLive) {
        Session session = sessionFactory.openSession();
        List<Game> games = new ArrayList<>();
        if (withLive){
            games = session.createQuery("FROM Game WHERE groupA.groupName = :groupNameA or groupB.groupName = :groupNameB")
                    .setParameter("groupNameA", teamRank.getGroupName())
                    .setParameter("groupNameB", teamRank.getGroupName())
                    .list();
        } else {
            games = session.createQuery("FROM Game WHERE (groupA.groupName = :groupNameA or groupB.groupName = :groupNameB) and isLive = false")
                    .setParameter("groupNameA", teamRank.getGroupName())
                    .setParameter("groupNameB", teamRank.getGroupName())
                    .list();
        }
        for (Game game: games){
            teamRank.updateGroupDetails(game);
        }
        session.close();
    }
    public void saveGame(String group1Name,String group2Name, String token){
        Game game = new Game();
        game.setGroupA(getGroupByGroupName(group1Name));
        game.setGroupB(getGroupByGroupName(group2Name));
        game.setLive(true);
        game.setGoalsGroupA(0);
        game.setGoalsGroupB(0);
        System.out.println("save Game");
        game.setUserManager(getUserByToken(token));
        sessionFactory.openSession().save(game);
    }
    public void finishGame(String group1Name,String group2Name){ // todo
        Session session = sessionFactory.openSession();
        Transaction transaction=session.beginTransaction();
        session.createQuery("update Game set isLive=false where groupA= :groupA AND groupB= :groupB")
                .setParameter("groupA",getGroupByGroupName(group1Name))
                .setParameter("groupB",getGroupByGroupName(group2Name))
                .executeUpdate();
        transaction.commit();
        session.close();
    }
    public void updateGoals(String groupAName,String groupBName,int goalsGroupA,int goalsGroupB){// todo
        Session session = sessionFactory.openSession();
        Transaction transaction=session.beginTransaction();
        session.createQuery("update Game set goalsGroupA=:goalsGroupA , goalsGroupB=:goalsGroupB where groupA= :groupA AND groupB= :groupB")
                .setParameter("groupA",getGroupByGroupName(groupAName))
                .setParameter("groupB",getGroupByGroupName(groupBName))
                .setParameter("goalsGroupA",goalsGroupA)
                .setParameter("goalsGroupB",goalsGroupB)
                .executeUpdate();
        transaction.commit();
        session.close();
    }
    public void saveUser(UserObject userObject){
        sessionFactory.openSession().save(userObject);
    }
    public boolean userNameExist(String userName) {
        boolean exist = false;
        Session session = sessionFactory.openSession();
        List<UserObject> users =session.createQuery("FROM UserObject where userName= :userName")
                .setParameter("userName",userName).list();
        session.close();
        if(users.size() == 1){
            exist = true;
        }
        return exist;
    }
    public String getUserByCreds (String userName, String token) {
        String response = null;
        Session session = sessionFactory.openSession();
        Query query = session.createQuery(" FROM UserObject where userName= :userName AND token= :token");
        query.setParameter("userName",userName);
        query.setParameter("token",token);
        List<UserObject> users = query.getResultList();
        if(users.size()==1) {
            response = users.get(0).getToken();
        }
        System.out.println(response);
        session.close();
        return response;
    }
    public UserObject getUserByDetails (String userName, String password) {
        Session session = sessionFactory.openSession();
        String token = utils.createHash(userName, password);
        List<UserObject> users = session.createQuery(" FROM UserObject where userName =:userName AND token =:token")
                .setParameter("userName",userName)
                .setParameter("token",token).list();
        if (users.size() == 1) {
            return users.get(0);
        }
        session.close();
        return null;
    }
    public UserObject getUserByToken (String token) {
        Session session = sessionFactory.openSession();
        List<UserObject> users = session.createQuery(" FROM UserObject where token = :token")
                .setParameter("token",token).list();
        session.close();
        if (users.size() == 1) {
            System.out.println("user: " + users.get(0));
            return users.get(0);
        }
        return null;
    }
    public void addUser (String userName, String password) {
        if(!userNameExist(userName)) {
            UserObject userObject = new UserObject();
            userObject.setUserName(userName);
            String token = utils.createHash(userName, password);
            userObject.setToken(token);
            saveUser(userObject);
        }
    }
}
