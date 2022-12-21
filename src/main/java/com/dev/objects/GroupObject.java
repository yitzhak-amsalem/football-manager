package com.dev.objects;

import javax.persistence.*;

@Entity
@Table(name = "teams")
public class GroupObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    public int id;
    @Column
    public String groupName;
    @Column
    private Boolean inLive;


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Boolean getInLive() {
        return inLive;
    }

    public void setInLive(boolean inLive) {
        this.inLive = inLive;
    }
}
