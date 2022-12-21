package com.dev.controllers;

import com.dev.objects.Game;
import com.dev.objects.GroupObject;
import com.dev.objects.TeamRankLive;
import com.dev.responses.BasicResponse;
import com.dev.utils.Persist;
import com.dev.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RestController
public class TestController {
    @Autowired
    public Utils utils;

    @Autowired
    private Persist persist;
    List<TeamRankLive> teams;
    List<GroupObject> allGroups = new ArrayList<>();

    @PostConstruct
    public void init () {

    }
    @RequestMapping(value = "/get-league-table", method = {RequestMethod.GET, RequestMethod.POST})
    public List<TeamRankLive> getTable (boolean withLive) {
        System.out.println(withLive);
        teams = new ArrayList<>();
        allGroups = persist.getAllGroups();
        for (GroupObject group: allGroups){
            TeamRankLive teamRank = new TeamRankLive(group.getGroupName(), 0,0,0,0,0);
            teams.add(teamRank);
        }
        for (TeamRankLive team: teams){
            System.out.println("--------");
            System.out.println(team);
            System.out.println("========");
            persist.getGroupDetails(team, withLive);
            System.out.println(team);
            System.out.println("--------");
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

    @RequestMapping(value = "/get-live-games", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Game> getLiveGames () {
        return persist.getLiveGames();
    }

    @RequestMapping(value = "/log-in", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse logIn (String username, String password) {
        System.out.println("username:"+username );

        BasicResponse basicResponse = null;
        String token = utils.createHash(username, password);
        System.out.println("token:"+token );
        token = persist.getUserByCreds(username, token); // todo rename
        if (token == null) {
            if (persist.userNameExist(username)) {
                basicResponse = new BasicResponse(false, 1);
            } else {
                basicResponse = new BasicResponse(false, 2);
            }
        } else {
            basicResponse = new BasicResponse(true, null);
        }
        return basicResponse;
    }



}
