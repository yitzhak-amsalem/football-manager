package com.dev.controllers;

import com.dev.objects.Game;
import com.dev.objects.GroupObject;
import com.dev.objects.TeamRankLive;
import com.dev.objects.UserObject;
import com.dev.responses.BasicResponse;
import com.dev.responses.SignInResponse;
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
    public List<TeamRankLive> getLeagueTable(boolean withLive) {
        System.out.println(withLive);
        teams = new ArrayList<>();
        allGroups = persist.getAllGroups();
        for (GroupObject group: allGroups){
            TeamRankLive teamRank = new TeamRankLive(group.getGroupName(), 0,0,0,0,0);
            teams.add(teamRank);
        }
        for (TeamRankLive team: teams){
            persist.getGroupDetails(team, withLive);
        }
        Collections.sort(teams);
        return teams;
    }

    @RequestMapping(value = "/save-game", method = {RequestMethod.GET, RequestMethod.POST})
    public void saveGame (String group1Name, String group2Name, String token) {
        persist.saveGame(group1Name, group2Name, token);
    }
    @RequestMapping(value = "/update-goals", method = {RequestMethod.GET, RequestMethod.POST})
    public void updateGoals (String groupAName,String groupBName,int goalsGroupA,int goalsGroupB)  {
        persist.updateGoals(groupAName,groupBName,goalsGroupA,goalsGroupB);
    }
    @RequestMapping(value = "/finish-game", method = {RequestMethod.GET, RequestMethod.POST})
    public void finishGame (String group1Name,String group2Name) {
        persist.finishGame(group1Name, group2Name);
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
    @RequestMapping(value = "/get-live-games-per-user", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Game> getLiveGamesPerUser (String token) {
        return persist.getLiveGamesPerUser(token);
    }
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login (String userName, String password) {
        BasicResponse basicResponse = null;
        String token = utils.createHash(userName, password);
        token = persist.getUserByCreds(userName, token);
        if (token == null) {
            if (persist.userNameExist(userName)) {
                basicResponse = new BasicResponse(false, 1); //todo constants
            } else {
                basicResponse = new BasicResponse(false, 2); //todo constants
            }
        } else {
            UserObject user = persist.getUserByToken(token);
            basicResponse = new SignInResponse(true, null, user);
        }
        return basicResponse;
    }
}
