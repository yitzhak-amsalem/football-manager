
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
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    @Autowired
    private Utils utils;

    public void initBasicDetails() {
        setGroups();
        setFirstUser();
    }

    public void setFirstUser() {
        addUser("manager", "12345678");
        addUser("manager1", "123456781");
    }

    public void setGroups() {
        Session session = sessionFactory.openSession();
        List<GroupObject> groups = getAllGroups();
        if (groups.size() == 0) {
            String[] groupsNames = {"Man City", "Man United", "Chelsea"
                    , "Barcelona", "Real Madrid", "Seville"
                    , "PSG", "Marseille", "Juventus"
                    , "Inter", "Bayern Munich", "Dortmund"};
            for (String groupName : groupsNames) {
                GroupObject group = new GroupObject();
                group.setGroupName(groupName);
                session.save(group);
            }
        }
    }

    public GroupObject getGroupByGroupName(String groupName) {
        Session session = sessionFactory.openSession();
        List<GroupObject> availableGroups = session.createQuery("FROM GroupObject WHERE groupName = :groupName")
                .setParameter("groupName", groupName).list();
        session.close();
        if (availableGroups.size() == 1) {
            return availableGroups.get(0);
        }
        return null;
    }

    public List<GroupObject> getAvailableGroups() {
        Session session = sessionFactory.openSession();
        List<GroupObject> availableGroups = getAllGroups();
        List<Game> gamesInLive = session.createQuery("FROM Game WHERE isLive = true").list();
        List<GroupObject> groupsInLive = new ArrayList<>();
        for (Game game : gamesInLive) {
            groupsInLive.add(game.getGroupA());
            groupsInLive.add(game.getGroupB());
        }
        availableGroups.removeAll(groupsInLive);
        session.close();
        return availableGroups;
    }

    public List<GroupObject> getAllGroups() {
        Session session = sessionFactory.openSession();
        List<GroupObject> groups = session.createQuery("FROM GroupObject ").list();
        session.close();
        return groups;
    }

    public List<Game> getLiveGames() {
        Session session = sessionFactory.openSession();
        List<Game> liveGames = session.createQuery("FROM Game WHERE isLive = true ")
                .list();
        session.close();
        return liveGames;
    }

    public List<Game> getLiveGamesPerUser(String token) {
        Session session = sessionFactory.openSession();
        List<Game> liveGamesPerUser = session.createQuery("FROM Game WHERE isLive = true AND userManager = :user")
                .setParameter("user", getUserByToken(token))
                .list();
        session.close();
        return liveGamesPerUser;
    }

    public void getGroupDetails(TeamRankLive teamRank, boolean withLive) {
        Session session = sessionFactory.openSession();
        List<Game> games = new ArrayList<>();
        if (withLive) {
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
        for (Game game : games) {
            teamRank.updateGroupDetails(game);
        }
        session.close();
    }

    public void saveGame(String groupAName, String groupBName, String token) {
        Game game = new Game();
        game.setGroupA(getGroupByGroupName(groupAName));
        game.setGroupB(getGroupByGroupName(groupBName));
        game.setLive(true);
        game.setGoalsGroupA(0);
        game.setGoalsGroupB(0);
        game.setUserManager(getUserByToken(token));
        sessionFactory.openSession().save(game);
    }

    public void updateGoals(String groupAName, String groupBName, int goalsGroupA, int goalsGroupB) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.createQuery("update Game set goalsGroupA=:goalsGroupA , goalsGroupB=:goalsGroupB " +
                        "where groupA= :groupA AND groupB= :groupB AND isLive = true ")
                .setParameter("groupA", getGroupByGroupName(groupAName))
                .setParameter("groupB", getGroupByGroupName(groupBName))
                .setParameter("goalsGroupA", goalsGroupA)
                .setParameter("goalsGroupB", goalsGroupB)
                .executeUpdate();
        transaction.commit();
        session.close();
    }

    public void finishGame(String groupAName, String groupBName) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.createQuery("update Game set isLive=false where groupA= :groupA AND groupB= :groupB AND isLive = true")
                .setParameter("groupA", getGroupByGroupName(groupAName))
                .setParameter("groupB", getGroupByGroupName(groupBName))
                .executeUpdate();
        transaction.commit();
        session.close();
    }

    public void saveUser(UserObject userObject) {
        sessionFactory.openSession().save(userObject);
    }

    public boolean userNameExist(String userName) {
        boolean exist = false;
        Session session = sessionFactory.openSession();
        List<UserObject> users = session.createQuery("FROM UserObject where userName= :userName")
                .setParameter("userName", userName).list();
        session.close();
        if (users.size() == 1) {
            exist = true;
        }
        return exist;
    }

    public UserObject getUserByToken(String token) {
        Session session = sessionFactory.openSession();
        List<UserObject> users = session.createQuery(" FROM UserObject where token = :token")
                .setParameter("token", token).list();
        session.close();
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }

    public void addUser(String userName, String password) {
        if (!userNameExist(userName)) {
            UserObject userObject = new UserObject();
            userObject.setUserName(userName);
            String token = utils.createHash(userName, password);
            userObject.setToken(token);
            saveUser(userObject);
        }
    }
}
