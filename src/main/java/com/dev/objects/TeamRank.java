package com.dev.objects;


public class TeamRank implements Comparable<TeamRank>{
    public String groupName;
    private int losses;
    private int wins;
    private int draws;
    private int goalsForward;
    private int goalsAgainst;

    public TeamRank(String groupName, int losses, int wins, int draws, int goalsForward, int goalsAgainst) {
        this.groupName = groupName;
        this.losses = losses;
        this.wins = wins;
        this.draws = draws;
        this.goalsForward = goalsForward;
        this.goalsAgainst = goalsAgainst;
    }
    public void updateGroupDetails(Game game){
        if (game.getGroupA().getGroupName().equals(this.groupName)){
            this.update(game.getGoalsGroupA(), game.getGoalsGroupB());
        } else if (game.getGroupB().getGroupName().equals(this.groupName)) {
            this.update(game.getGoalsGroupB(), game.getGoalsGroupA());
        }
    }
    private void update(int goalsGroupA, int goalsGroupB){
        if (goalsGroupA > goalsGroupB) {
            this.setWins(this.getWins() + 1);
        } else {
            if (goalsGroupA < goalsGroupB) {
                this.setLosses(this.getLosses() + 1);
            } else {
                this.setDraws(this.getDraws() + 1);
            }
        }
        this.setGoalsAgainst(this.getGoalsAgainst() + goalsGroupB);
        this.setGoalsForward(this.getGoalsForward() + goalsGroupA);
    }

    @Override
    public int compareTo(TeamRank otherTeam) {
        return ((otherTeam.wins * 3 + otherTeam.draws) - (this.wins * 3 + this.draws) != 0) ?
                    ((otherTeam.wins * 3 + otherTeam.draws) - (this.wins * 3 + this.draws))
                    :
                    ((otherTeam.goalsForward - otherTeam.goalsAgainst) - (this.goalsForward - this.goalsAgainst) != 0) ?
                        ((otherTeam.goalsForward - otherTeam.goalsAgainst) - (this.goalsForward - this.goalsAgainst))
                        :
                        (this.groupName.compareTo(otherTeam.groupName));
    }

    @Override
    public String toString() {
        return "TeamRank{" +
                "groupName='" + groupName + '\'' +
                ", losses=" + losses +
                ", wins=" + wins +
                ", draws=" + draws +
                ", goalsForward=" + goalsForward +
                ", goalsAgainst=" + goalsAgainst +
                '}';
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getGoalsForward() {
        return goalsForward;
    }

    public void setGoalsForward(int goalsForward) {
        this.goalsForward = goalsForward;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }


}
