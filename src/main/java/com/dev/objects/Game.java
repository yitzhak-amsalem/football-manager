package com.dev.objects;

import javax.persistence.*;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    public int id;
    @ManyToOne
    @JoinColumn
    private GroupObject groupA;

    @ManyToOne
    @JoinColumn
    private GroupObject groupB;
    @Column
    private int goalsGroupA;
    @Column
    private int goalsGroupB;
    @Column
    private Boolean isLive;

    @ManyToOne
    @JoinColumn
    private UserObject userManager; // todo update connections func

    @Override
    public String toString() {
        return "Game{" +
                "groupA=" + groupA +
                ", groupB=" + groupB +
                ", goalsGroupA=" + goalsGroupA +
                ", goalsGroupB=" + goalsGroupB +
                ", isLive=" + isLive +
                '}';
    }

    public GroupObject getGroupA() {
        return groupA;
    }

    public void setGroupA(GroupObject groupA) {
        this.groupA = groupA;
    }

    public GroupObject getGroupB() {
        return groupB;
    }

    public void setGroupB(GroupObject groupB) {
        this.groupB = groupB;
    }

    public int getGoalsGroupA() {
        return goalsGroupA;
    }

    public void setGoalsGroupA(int goalsGroupA) {
        this.goalsGroupA = goalsGroupA;
    }

    public int getGoalsGroupB() {
        return goalsGroupB;
    }

    public void setGoalsGroupB(int goalsGroupB) {
        this.goalsGroupB = goalsGroupB;
    }

    public Boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}
