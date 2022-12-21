
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

    }
    public void setFirstUser(){
        UserObject userObject = new UserObject();
        String username="manager";
        String password="12345678";
        userObject.setUsername(username);
        String token = utils.createHash(username, password);
        userObject.setToken(token);
        if(!userNameExist(username)) {
            saveUser(userObject);
        }
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
        game4.setLive(true);
        session.save(game4);
        Game game5 = new Game();
        game5.setGroupA(getGroupByGroupName("Real Madrid"));
        game5.setGroupB(getGroupByGroupName("Barcelona"));
        game5.setGoalsGroupA(2);
        game5.setGoalsGroupB(6);
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
    public List<Game> getLiveGames () {
        Session session = sessionFactory.openSession();
        List<Game> liveGames = session.createQuery("FROM Game WHERE isLive = true ")
                .list();
        session.close();
        System.out.println(liveGames.toString());
        return liveGames;
    }
    public void setGroupInLive(String groupName){
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        GroupObject groupToUpdate = session.get(GroupObject.class, getGroupByGroupName(groupName).id);
        groupToUpdate.setInLive(true);
        session.update(groupToUpdate);
        transaction.commit();
        session.close();
    }

    public void getGroupDetails (TeamRankLive teamRank, boolean withLive) {
        Session session = sessionFactory.openSession();
        List<Game> games = new ArrayList<>();
        if (withLive){
            System.out.println("in true");
            games = session.createQuery("FROM Game WHERE groupA.groupName = :groupNameA or groupB.groupName = :groupNameB")
                    .setParameter("groupNameA", teamRank.getGroupName())
                    .setParameter("groupNameB", teamRank.getGroupName())
                    .list();
        } else {
            System.out.println("in false");
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

    public void saveUser(UserObject userObject){
        sessionFactory.openSession().save(userObject);
    }

    public boolean userNameExist(String username) {
        boolean exist = false;
        Session session = sessionFactory.openSession();
        List<UserObject> users =session.createQuery("FROM UserObject where username= :username")
                .setParameter("username",username).list();
        session.close();
        if(users.size() == 1){
            exist = true;
        }
        return exist;
    }

    public String getUserByCreds (String username, String token) {
        String response = null;
        Session session = sessionFactory.openSession();
        Query query = session.createQuery(" FROM UserObject where username= :username AND token= :token");
        query.setParameter("username",username);
        query.setParameter("token",token);
        List<UserObject> users = query.getResultList();
        if(users.size()==1) {
            response = users.get(0).getToken();
        }
        System.out.println(response);
        session.close();
        return response;
    }

}
