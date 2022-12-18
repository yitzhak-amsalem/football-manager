package com.dev.controllers;

import com.dev.objects.GroupObject;
import com.dev.objects.TeamRank;
import com.dev.objects.User;
import com.dev.responses.BasicResponse;
import com.dev.responses.SignInResponse;
import com.dev.utils.Persist;
import com.dev.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.persistence.Table;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RestController
public class TestController {

    private List<User> myUsers = new ArrayList<>();

    @Autowired
    public Utils utils;


    @Autowired
    private Persist persist;


    List<GroupObject> allGroups = new ArrayList<>();

    @PostConstruct
    public void init () {

    }

    @RequestMapping(value = "/get-league-table", method = {RequestMethod.GET, RequestMethod.POST})
    public List<TeamRank> getTable () {
        List<TeamRank> teams = new ArrayList<>();
        persist.setGame();
        allGroups = persist.getAllGroups();
        for (GroupObject group: allGroups){
            TeamRank teamRank = new TeamRank(group.getGroupName(), 0,0,0,0,0);
            teams.add(teamRank);
        }
        for (TeamRank team: teams){
            persist.getGroupDetails(team);
        }
        Collections.sort(teams);
        return teams;
    }

    @RequestMapping(value = "/set-group-in-live", method = {RequestMethod.GET, RequestMethod.POST})
    public void setGroupInLive (String groupName) {
        persist.setGroupInLive(groupName);
    }
    @RequestMapping(value = "/get-available-groups", method = {RequestMethod.GET, RequestMethod.POST})
    public List<String> getAvailableGroups () {
        List<String> availableGroupsNames = new ArrayList<>();
        List<GroupObject> groups = persist.getAvailableGroups();
        for (GroupObject group: groups){
            availableGroupsNames.add(group.getGroupName());
        }
        return availableGroupsNames; // todo all group details or group name only
    }


    @RequestMapping(value = "/log-in", method = RequestMethod.POST)
    public BasicResponse logIn (String username, String password) {
        System.out.println("username:"+username );

        BasicResponse basicResponse = null;
        String token = createHash(username, password);
        System.out.println("token:"+token );
        token = persist.getUserByCreds(username, token);
        if (token == null) {
            System.out.println("token:"+token );
            if (persist.usernameExist(username)) {
                basicResponse = new BasicResponse(false, 1);
            } else {
                basicResponse = new BasicResponse(false, 2);
            }
        } else {
            basicResponse = new BasicResponse(true, null);
        }
        return basicResponse;
    }

/*    @RequestMapping(value = "/create-account", method = {RequestMethod.GET, RequestMethod.POST})
    public User createAccount (String username, String password) {
        User newAccount = null;
        if (utils.validateUsername(username)) {
            if (utils.validatePassword(password)) {
                if (persist.usernameAvailable(username)) {
                    String token = createHash(username, password);
                    newAccount = new User(username, token);
                    persist.addUser(username, token);
                } else {
                    System.out.println("username already exits");
                }
            } else {
                System.out.println("password is invalid");
            }
        } else {
            System.out.println("username is invalid");
        }
        return newAccount;
    }*/


    public String createHash (String username, String password) {
        String raw = String.format("%s_%s", username, password);
        String myHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(raw.getBytes());
            byte[] digest = md.digest();
            myHash = DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return myHash;
    }

    private boolean checkIfUsernameExists (String username) {
        boolean exists = false;
        for (User user : this.myUsers) {
            if (user.getUsername().equals(username)) {
                exists = true;
                break;
            }
        }

        return exists;
    }







}
