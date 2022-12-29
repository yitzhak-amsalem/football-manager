package com.dev.controllers;

import com.dev.objects.Game;
import com.dev.objects.GroupObject;
import com.dev.objects.TeamRankLive;
import com.dev.objects.UserObject;
import com.dev.responses.BasicResponse;
import com.dev.responses.SignInResponse;
import com.dev.utils.Constants;
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
    private List<TeamRankLive> teams;
    private List<TeamRankLive> liveTeams;
    private List<GroupObject> allGroups = new ArrayList<>();

    @PostConstruct
    public void init() {
        persist.initBasicDetails();
        allGroups = persist.getAllGroups();
    }

    @RequestMapping(value = "/get-league-table", method = {RequestMethod.GET, RequestMethod.POST})
    public List<TeamRankLive> getLeagueTable() {
        teams = new ArrayList<>();
        for (GroupObject group : allGroups) {
            TeamRankLive teamRank = new TeamRankLive(group.getGroupName(), 0, 0, 0, 0, 0);
            teams.add(teamRank);
        }
        for (TeamRankLive team : teams) {
            persist.getGroupDetails(team);
        }
        Collections.sort(teams);
        return teams;
    }
    @RequestMapping(value = "/get-league-table-live", method = {RequestMethod.GET, RequestMethod.POST})
    public List<TeamRankLive> getLeagueTableLive() {
        liveTeams = new ArrayList<>();
        for (GroupObject group : allGroups) {
            TeamRankLive teamRank = new TeamRankLive(group.getGroupName(), 0, 0, 0, 0, 0);
            liveTeams.add(teamRank);
        }
        for (TeamRankLive team : liveTeams) {
            persist.getGroupLiveDetails(team);
        }
        Collections.sort(liveTeams);
        return liveTeams;
    }

    @RequestMapping(value = "/save-game", method = {RequestMethod.GET, RequestMethod.POST})
    public void saveGame(String groupAName, String groupBName, String token) {
        persist.saveGame(groupAName, groupBName, token);
    }

    @RequestMapping(value = "/update-goals", method = {RequestMethod.GET, RequestMethod.POST})
    public void updateGoals(String groupAName, String groupBName, int goalsGroupA, int goalsGroupB) {
        persist.updateGoals(groupAName, groupBName, goalsGroupA, goalsGroupB);
    }

    @RequestMapping(value = "/end-game", method = {RequestMethod.GET, RequestMethod.POST})
    public void endGame(String groupAName, String groupBName) {
        persist.finishGame(groupAName, groupBName);
    }

    @RequestMapping(value = "/get-available-groups", method = {RequestMethod.GET, RequestMethod.POST})
    public List<String> getAvailableGroups() {
        List<String> availableGroupsNames = new ArrayList<>();
        List<GroupObject> groups = persist.getAvailableGroups();
        for (GroupObject group : groups) {
            availableGroupsNames.add(group.getGroupName());
        }
        return availableGroupsNames;
    }

    @RequestMapping(value = "/get-live-games", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Game> getLiveGames() {
        return persist.getLiveGames();
    }

    @RequestMapping(value = "/get-live-games-per-user", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Game> getLiveGamesPerUser(String token) {
        return persist.getLiveGamesPerUser(token);
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login(String userName, String password) {
        BasicResponse basicResponse = null;
        String token = utils.createHash(userName, password);
        UserObject user = persist.getUserByToken(token);
        if (user == null) {
            if (persist.userNameExist(userName)) {
                basicResponse = new BasicResponse(false, Constants.PASSWORD_FAILED);
            } else {
                basicResponse = new BasicResponse(false, Constants.USER_NAME_FAILED);
            }
        } else {
            basicResponse = new SignInResponse(true, null, user.getToken());
        }
        return basicResponse;
    }
}
